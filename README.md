# Alarme Motivacional

Aplicativo simples de alarme motivacional em Kotlin. Os pontos abaixo explicam como as permissões são tratadas, incluindo o fluxo especial para MIUI.

## Permissões e agendamentos
- **Notificações (Android 13+)**: a `MainActivity`/`AlarmActivity` solicita `POST_NOTIFICATIONS` na primeira abertura. Sem essa permissão o `AlarmService` não inicia o modo foreground e o alarme não será exibido.
- **Alarmes exatos (Android 12+)**: o app tenta abrir a tela de `SCHEDULE_EXACT_ALARM`. Caso o usuário negue, o agendamento é abortado e o alarme é salvo como inativo, exibindo uma mensagem para habilitar a permissão.
- **Leitura de mídia**: migrado para `READ_MEDIA_AUDIO`/`READ_MEDIA_VIDEO` (ou `READ_EXTERNAL_STORAGE` até o Android 12). A permissão é solicitada somente quando o usuário clica para escolher um som/vídeo.

## Fluxo especial para MIUI
O helper `MiuiHelper` continua orientando dispositivos Xiaomi/MIUI:
- Detecta o fabricante.
- Verifica se `canScheduleExactAlarms` está liberado.
- Exibe um diálogo guiando o usuário até `ACTION_REQUEST_SCHEDULE_EXACT_ALARM` ou para a tela de detalhes do app caso a primeira falhe.

## Comportamento quando permissões são negadas
- **Notificações**: o serviço do alarme é encerrado e um aviso é exibido para o usuário.
- **Alarmes exatos**: o agendamento não é feito para evitar falhas; o alarme é marcado como desativado e uma mensagem informa a necessidade da permissão.
- **Mídia**: o seletor de som/vídeo só abre após a concessão da permissão correspondente.
