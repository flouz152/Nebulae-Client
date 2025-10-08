# Nebulae Target ESP LabyMod Addon

Готовый к сборке проект аддона находится в каталоге [`addons/targetesp`](addons/targetesp). Он использует API LabyMod 3 и повторяет визуальное оформление оригинального TargetESP.

## Быстрый старт

```bash
cd addons/targetesp
gradle build
```

Собранный файл появится в `addons/targetesp/build/libs/targetesp-addon-1.0.0.jar`.

## Текстуры

| Где взять | Куда положить |
| --- | --- |
| `src/assets/minecraft/night/image/target/Quad.png` | `assets/minecraft/night/image/target/Quad.png` внутри JAR аддона |
| `src/assets/minecraft/night/image/target/Quad2.png` | `assets/minecraft/night/image/target/Quad2.png` внутри JAR аддона |
| `src/assets/minecraft/night/image/glow.png` | `assets/minecraft/night/image/glow.png` внутри JAR аддона |

Gradle-скрипт аддона автоматически копирует перечисленные ресурсы, поэтому дополнительные действия не требуются.
