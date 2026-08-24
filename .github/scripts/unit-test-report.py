#!/usr/bin/env python3

from __future__ import annotations

import os
import re
import xml.etree.ElementTree as ET
from pathlib import Path

RESULTS_GLOB = os.environ.get("RESULTS_GLOB", "**/build/test-results/**/*.xml")
REPORT_FILE = os.environ.get("REPORT_FILE", "unit-test-report.md")
MAX_FAILURES = int(os.environ.get("MAX_FAILURES", "15"))
REPO = os.environ.get("GITHUB_REPOSITORY", "")
SHA = os.environ.get("HEAD_SHA") or os.environ.get("GITHUB_SHA", "")
WORKSPACE = Path(os.environ.get("GITHUB_WORKSPACE", ".")).resolve()

SOURCE_SUFFIXES = (".kt", ".java", ".kts")

def index_sources() -> dict[str, str]:
    """Mapeia 'NomeDaClasse' -> caminho relativo do arquivo no repositorio."""
    index: dict[str, str] = {}
    for path in WORKSPACE.rglob("*"):
        if path.suffix not in SOURCE_SUFFIXES or not path.is_file():
            continue
        if any(part in {"build", ".git", ".gradle"} for part in path.parts):
            continue
        index.setdefault(path.stem, str(path.relative_to(WORKSPACE)).replace("\\", "/"))
    return index


SOURCES = index_sources()


def source_path(classname: str) -> str | None:
    simple = classname.split(".")[-1].split("$")[0]
    return SOURCES.get(simple)


def line_of(classname: str, trace: str) -> int | None:
    """Extrai a linha da falha no proprio arquivo de teste, via stack trace."""
    simple = classname.split(".")[-1].split("$")[0]
    match = re.search(rf"\({re.escape(simple)}\.(?:kt|java):(\d+)\)", trace or "")
    return int(match.group(1)) if match else None


def link(path: str, line: int | None) -> str:
    if not REPO or not SHA:
        return f"`{path}`"
    anchor = f"#L{line}" if line else ""
    label = f"{path}:{line}" if line else path
    return f"[`{label}`](https://github.com/{REPO}/blob/{SHA}/{path}{anchor})"


def short(text: str, limit: int = 160) -> str:
    one_line = " ".join((text or "").split())
    return one_line[: limit - 1] + "…" if len(one_line) > limit else one_line

total = passed = failed = skipped = 0
failures: list[dict] = []
files_seen = 0

for xml_file in sorted(WORKSPACE.glob(RESULTS_GLOB)):
    try:
        root = ET.parse(xml_file).getroot()
    except ET.ParseError:
        continue
    files_seen += 1

    suites = [root] if root.tag == "testsuite" else root.iter("testsuite")
    for suite in suites:
        for case in suite.iter("testcase"):
            total += 1
            classname = case.get("classname", suite.get("name", "?"))
            name = case.get("name", "?")

            if case.find("skipped") is not None:
                skipped += 1
                continue

            problem = case.find("failure")
            if problem is None:
                problem = case.find("error")
            if problem is None:
                passed += 1
                continue

            failed += 1
            trace = (problem.text or "") + " " + (problem.get("message") or "")
            failures.append(
                {
                    "classname": classname,
                    "name": name,
                    "message": problem.get("message") or short(problem.text or ""),
                    "file": source_path(classname),
                    "line": line_of(classname, trace),
                }
            )

executed = passed + failed  # testes ignorados nao entram no percentual
rate = (passed / executed * 100) if executed else 0.0
status = "success" if failed == 0 and total > 0 else "failure"

md: list[str] = []

if files_seen == 0 or total == 0:
    md += [
        "## ⚠️ Testes Unitários",
        "",
        "Nenhum resultado de teste foi encontrado.",
        f"Padrão de busca: `{RESULTS_GLOB}`",
    ]
    status = "failure"
else:
    icon = "✅" if failed == 0 else "❌"
    title = "Testes Unitários" if failed == 0 else "Testes Unitários — falhas encontradas"
    bar_len = 20
    full = round(rate / 100 * bar_len)
    bar = "█" * full + "░" * (bar_len - full)

    md += [
        f"## {icon} {title}",
        "",
        f"`{bar}` **{rate:.1f}%** — {passed} de {executed} testes executados passaram",
        "",
        "| Total | ✅ Passou | ❌ Falhou | ⏭️ Ignorado |",
        "| ----: | --------: | --------: | ----------: |",
        f"| {total} | {passed} | {failed} | {skipped} |",
    ]

    if failed:
        by_file: dict[str, list[dict]] = {}
        for item in failures:
            by_file.setdefault(item["file"] or item["classname"], []).append(item)

        md += ["", "### Arquivos com falha", ""]
        shown = 0
        for path, items in sorted(by_file.items(), key=lambda kv: -len(kv[1])):
            first_line = next((i["line"] for i in items if i["line"]), None)
            location = link(path, first_line) if "/" in path else f"`{path}`"
            md.append(f"- {location} — **{len(items)}** falha(s)")
            for item in items:
                if shown >= MAX_FAILURES:
                    break
                md.append(f"  - `{item['name']}` → {short(item['message'])}")
                shown += 1
            if shown >= MAX_FAILURES:
                break

        if failed > shown:
            md.append(f"- … e mais {failed - shown} falha(s). Veja o log completo do job.")

        md += [
            "",
            "**Como reproduzir localmente:**",
            "```bash",
            "./gradlew test",
            "```",
        ]
    else:
        md += ["", "Todos os testes passaram. 🎉"]

report = "\n".join(md) + "\n"
Path(REPORT_FILE).write_text(report, encoding="utf-8")
print(report)

if out := os.environ.get("GITHUB_OUTPUT"):
    with open(out, "a", encoding="utf-8") as fh:
        fh.write(f"status={status}\n")
        fh.write(f"total={total}\n")
        fh.write(f"passed={passed}\n")
        fh.write(f"failed={failed}\n")
        fh.write(f"skipped={skipped}\n")
        fh.write(f"rate={rate:.1f}\n")