## Как сделать jar файл из исходных кодов

Из папки `src/main/java` выполнить следующие команды:

    javac blood_speed/Main.java
    jar cfe speed.jar blood_speed.Main *
    
Появится файл `speed.jar`

## Как запустить jar файл

Выполнить команду
    
    java -jar speed.jar
    
Рядом с файлом `speed.jar` должен лежать `settings.ini` с актуальными данными.

Папки, которые указаны в settings.ini как output папки шагов обработки, создаются автоматически.

Пути до папок и файлов могут быть абсолютными или относительными, но в них нельзя использовать кириллические буквы.