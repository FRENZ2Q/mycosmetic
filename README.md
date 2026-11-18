# My Cosmetics (Fabric, Minecraft 1.21.4)

Личная косметика: плащи, шапки, маски, крылья/рюкзаки, хвосты, питомцы.
Меню: клавиша **K** (слева выбираешь категорию, в центре предмет, справа предпросмотр).
Видна только тебе (клиентский мод). Выбор хранится в `config/mycosmetics.json`.

Сборка: JDK 21 + Gradle 8.12+: `gradle wrapper --gradle-version 8.12` и `./gradlew build`.
Готовый мод: `build/libs/mycosmetics-1.0.0.jar` (не -sources). Нужен Fabric API.
Без установки Gradle: залей проект на GitHub, вкладка Actions соберёт jar.

Новая косметика добавляется в `Cosmetics.java` как набор цветных блоков.
