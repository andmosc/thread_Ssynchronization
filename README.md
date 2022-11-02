## Задача 2. Частота операций

## Описание

В процессе создания программы для робота-доставщика вы решили сделать так, чтобы каждый раз когда обновляется мапа `sizeToFreq` на экран выводился бы текущий лидер среди частот. Для этого придётся просматривать весь `Map` в поисках лидера, что достаточно затратно.

Вместо того чтобы делать это в том же потоке, что и считал размер, заведите один отдельный поток, который будет заниматься только подсчётом максимума в мапе и выводом этой информации на экран. Делать он это будет в цикле, а чтобы подсчёт и вывод происходили только когда нужно, он будет ждать "сигнала" через `wait-notify` от считающих потоков.

Условием цикла поставьте проверку на то, что поток не прервали. В основном потоке после `for` с `join` прервите этот выводящий максимумы поток.