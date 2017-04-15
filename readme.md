## Как сделать jar файл из исходных кодов

Из корня проекта выполнить следующие команды:

    javac -d bin -cp src/main/java -encoding UTF8 src/main/java/blood_speed/Main.java
    cd bin
    jar cfe speed.jar blood_speed.Main *
    
Появится файл `speed.jar`

Исходный код тут https://github.com/z17/BloodSpeed

## Как запустить jar файл

Выполнить команду
    
    java -jar speed.jar <command>
    
Рядом с файлом `speed.jar` должен лежать `settings.ini` с актуальными данными.

Папки, которые указаны в settings.ini как output папки шагов обработки, создаются автоматически.

Пути до папок и файлов могут быть абсолютными или относительными, но в них нельзя использовать кириллические буквы.

Примеры команд 

    Компенсация фона и создание контура:
    java -jar speed.jar -background
    
    Выделение ЦЛК:
    java -jar speed.jar -middle-line
    
    Трансформация вдоль ЦЛК:
    java -jar speed.jar -transform
    
    Определение скорости:
    java -jar speed.jar -speed