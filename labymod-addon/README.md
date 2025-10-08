# TargetESP LabyMod 3 Add-on

Этот модуль повторяет визуальные эффекты `TargetESP` из клиента Nebulae для LabyMod 3. Аддон активирует подсветку при ударе любой сущности и скрывает эффект, когда цель отходит более чем на 5 блоков. В настройках доступны все режимы оригинального эффекта: "Призраки", "Круг", "Квадрат" и "Новый квадрат".

## Основные файлы

- `addon.json` — метаданные аддона и точка входа `dev.nebulae.targetesp.TargetEspAddon`.
- `src/main/java/dev/nebulae/targetesp` — код логики отслеживания цели, конфигурации и отрисовки.
- `src/main/resources/assets/targetesp/textures` — папка для оригинальных текстур эффекта.

## Текстуры

Оригинальные файлы `glow.png`, `quad.png` и `quad_new.png` не входят в репозиторий. Скопируй их из установленного клиента Nebulae (папка `assets/targetesp/textures`) и помести в `labymod-addon/src/main/resources/assets/targetesp/textures` перед сборкой аддона.

## Сборка

Проект можно собрать как стандартный LabyMod 3 аддон, подключив зависимости API LabyMod и Forge 1.16.5. Точка входа аддона — класс `TargetEspAddon`.
