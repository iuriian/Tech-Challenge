-- Corrige valor de status inserido incorretamente no seed inicial.
-- O enum ServicoStatus usa FINALIZADA, mas o seed inseriu FINALIZADO.
UPDATE servicos SET status = 'FINALIZADA' WHERE status = 'FINALIZADO';
