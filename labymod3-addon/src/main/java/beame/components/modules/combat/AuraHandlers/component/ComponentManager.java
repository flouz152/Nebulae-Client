package beame.components.modules.combat.AuraHandlers.component;

import beame.Essence;
import beame.components.modules.combat.AuraHandlers.component.core.combat.RotationComponent;
import beame.components.modules.combat.AuraHandlers.component.core.combat.SmoothRotationComponent;

import java.util.HashMap;

public final class ComponentManager extends HashMap<Class<? extends Component>, Component> {
// leaked by itskekoff; discord.gg/sk3d dZThs6yu

    public void init() {
        add(
                new RotationComponent(),
                new SmoothRotationComponent()
        );

        this.values().forEach(component -> Essence.getHandler().getEventBus().register(component));
    }

    public void add(Component... components) {
        for (Component component : components) {
            this.put(component.getClass(), component);
        }
    }

    public <T extends Component> T get(final Class<T> clazz) {
        return this.values()
                .stream()
                .filter(component -> component.getClass() == clazz)
                .map(clazz::cast)
                .findFirst()
                .orElse(null);
    }
}