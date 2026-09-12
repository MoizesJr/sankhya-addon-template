# Idempotencia futura para e-mail ao vendedor

Esta primeira versao do POC nao implementa persistencia de idempotencia e nao altera banco.

## Resultado validado em ambiente local

Teste funcional validado em 12/09/2026:

- `NUNOTA`: 365
- `NUMNOTA`: 16
- `TOP`: 1101
- `CODVEND`: 2
- Destinatario: `ti@vitanutri.ind.br`
- SMTP do Sankhya configurado e validado pela tela Contas SMTP.
- E-mail recebido no Gmail.

Fluxo validado:

1. Callback `PROCESS_CONFIRMATION AFTER` disparou.
2. Service validou a nota, a TOP, o movimento, o status confirmado, o vendedor e o e-mail.
3. Gateway chamou `FilaMsgUtil.enviaEmail(...)` usando a fila nativa do Sankhya.
4. E-mail foi enfileirado e enviado pelo SMTP configurado no Sankhya.

Ressalvas do POC:

- O ambiente local ainda apresenta erro fiscal de `AssinadorNFe`/certificado para a NF-e.
- Esta versao ainda nao envia DANFE.
- Esta versao ainda nao anexa XML.
- Esta versao ainda nao valida autorizacao SEFAZ antes do envio.
- Esta versao nao deve enviar e-mail quando o evento de confirmacao retornar erro, exceto se a flag temporaria `PERMITIR_ENVIO_COM_ERRO_EVENTO_POC` for ativada explicitamente.

Para uma versao segura de producao, criar uma tabela propria no Dicionario de Dados, por exemplo `AD_ENV_DANFE_VEND`, com chave unica para:

- `NUNOTA`
- `CODVEND`
- `TIPONOTIF`

Fluxo recomendado:

1. Antes de enfileirar, consultar a tabela propria pela combinacao `NUNOTA + CODVEND + TIPONOTIF`.
2. Se ja existir status `PENDENTE` ou `ENVIADO`, nao enfileirar novamente.
3. Se nao existir, inserir registro proprio com status `PENDENTE`.
4. Apos sucesso em `FilaMsgUtil.enviaEmail(...)`, atualizar para `ENVIADO`.
5. Em falha tecnica, registrar `ERRO` sem bloquear a confirmacao da nota.

Nao usar colecao `static` como mecanismo principal, porque ela perde estado em redeploy, restart e multiplas instancias.
Nao inserir diretamente em tabelas internas de fila de mensagens do Sankhya.
