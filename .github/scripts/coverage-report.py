#!/usr/bin/env python3

from __future__ import annotations

import os
import xml.etree.ElementTree as ET
from pathlib import Path

REPORTS_GLOB = os.environ.get("REPORTS_GLOB", "**/build/reports/jacoco/**/*.xml")
REPORT_FILE = os.environ.get("REPORT_FILE", "coverage-report.md")
MINIMUM = float(os.environ.get("MINIMUM", "80"))
METRIC = os.environ.get("METRIC", "LINE").upper()  # LINE | BRANCH | INSTRUCTION
WORKSPACE = Path(os.environ.get("GITHUB_WORKSPACE", ".")).resolve()

LABELS = {
    "INSTRUCTION": "Instruções",
    "BRANCH": "Branches",
    "LINE": "Linhas",
    "METHOD": "Métodos",
    "CLASS": "Classes",
}
ORDER = ["LINE", "BRANCH", "INSTRUCTION", "METHOD", "CLASS"]

totals: dict[str, dict[str, int]] = {}
files_seen = 0

for xml_file in sorted(WORKSPACE.glob(REPORTS_GLOB)):
    try:
        root = ET.parse(xml_file).getroot()
    except ET.ParseError:
        continue
    if root.tag != "report":
        continue
    files_seen += 1

    for counter in root.findall("counter"):
        kind = counter.get("type", "")
        bucket = totals.setdefault(kind, {"missed": 0, "covered": 0})
        bucket["missed"] += int(counter.get("missed", 0))
        bucket["covered"] += int(counter.get("covered", 0))


def percent(kind: str) -> float | None:
    data = totals.get(kind)
    if not data:
        return None
    total = data["missed"] + data["covered"]
    return (data["covered"] / total * 100) if total else 0.0


current = percent(METRIC)
status = "success" if current is not None and current >= MINIMUM else "failure"

md: list[str] = []

if files_seen == 0 or current is None:
    md += [
        "## ⚠️ Cobertura de Testes",
        "",
        "Nenhum relatório do JaCoCo foi encontrado.",
        f"Padrão de busca: `{REPORTS_GLOB}`",
        "",
        "Verifique se o plugin `jacoco` está aplicado e se a task "
        "`jacocoTestReport` gera o XML (`xml.required = true`).",
    ]
    status = "failure"
else:
    icon = "✅" if status == "success" else "❌"
    bar_len = 20
    full = round(current / 100 * bar_len)
    bar = "█" * full + "░" * (bar_len - full)
    diff = current - MINIMUM

    md += [
        f"## {icon} Cobertura de Testes",
        "",
        f"`{bar}` **{current:.1f}%** ({LABELS.get(METRIC, METRIC)})",
        "",
        f"| Cobertura atual | Mínimo exigido |",
        f"| --------------: | -------------: |",
        f"| **{current:.1f}%** | {MINIMUM:.1f}% |"
    ]

    detail = [k for k in ORDER if k != METRIC and percent(k) is not None]
    if detail:
        md += [
            "",
            "<details><summary>Demais métricas</summary>",
            "",
            "| Métrica | Cobertura |",
            "| :------ | --------: |",
        ]
        for kind in detail:
            md.append(f"| {LABELS.get(kind, kind)} | {percent(kind):.1f}% |")
        md += ["", "</details>"]

    if status != "success":
        md += [
            "",
            "**Como corrigir:** escreva testes para as classes menos cobertas e rode "
            "`./gradlew jacocoTestReport` para ver o relatório HTML em "
            "`build/reports/jacoco/test/html/index.html`.",
        ]

report = "\n".join(md) + "\n"
Path(REPORT_FILE).write_text(report, encoding="utf-8")
print(report)

if out := os.environ.get("GITHUB_OUTPUT"):
    with open(out, "a", encoding="utf-8") as fh:
        fh.write(f"status={status}\n")
        fh.write(f"coverage={current:.1f}\n" if current is not None else "coverage=0.0\n")
        fh.write(f"minimum={MINIMUM:.1f}\n")
        fh.write(f"metric={METRIC}\n")