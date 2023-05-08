# autocomplete
запускаем при помощи:
java -jar -Xmx7m .\target\AirportTask-1.0-SNAPSHOT.jar

Пишем условие и начало названия аэропорта.

Выводятся аэропорты в отсортированном виде, названия которых начинаются с указанных символов и которые удовлятворяют написанному условию.
Также выводится информация о количесве найденных аэропортов и о времени, которое потребовалось на их поиск.

Алгоритм работы такой:
0) До написания условия, csv файл парсится и индексы линий, на которых они стоят, а также названия аэропортов добавляются в мапу. Ключ - первая буква названия аэропорта. Значение - лист из элементов, в которых хранится индекс и названия аэропорта. Лист хранится в отсортированном виде.
1) Пишем условие(condition) и начало названия(in) аэрапорта
2) Парсим condition. Если в синтаксисе условия допущена ошибка, выводится надпись с указанием ошибки. И все возвращается к началу
3) Берем у мапы элемент с буквой, которой начинается in. У этого элемента при помощи бинарного поиска находим индекс строки, которая начинается с in.
4) Когда нашли индекс, идем вправо и влево добавляя все индексы в лист. Когда индексы всех элементов, которые начинаются на in добавили в массив, сортируем его.
5) Дальше идем по csv файлу, хватая только те строки, индекс которых есть в нашем массиве с индексами, и проверяем на наше условие. Остальные пропускаем.
6) Полученный список аэропортов сортируем и выводим в терминал.

Алгоритм проверки условия:
1) Создаем дек, если видим открытую скобку его индекс добавляем в стек.
2) Если видим закрывающую скобку, то удаляем верхний элемент, при этом добавляем удаленный элемент в переменную start и отправляем строку с start до текущего индекса в метод, где проверяется условие.
3) Условие проверяется так: создается лист листов(tfs), все добавляем в 0вой элемент tfs, когда видим знак 'или' то создаем еще один элемент в tfs и меняем индекс текущего элемента tfs. Если хотя бы в одном элементе tfs все элементы равны 'T', то условие выполняется и возвращается 'T';
4) В место условия, который находился в скобках, вставляется возвращенной символ 'T' или 'F'(результат условия, который находился в скобках)
5) Текущий индекс меняется на start и так повторяется пока скобки не пропадут.
6) В конце остается строка без скобок, его отправляем на проверку, и возвращенный символ будет результатом условия, если вернулось 'T' то условие выполнено, если 'F', то не выполнено
