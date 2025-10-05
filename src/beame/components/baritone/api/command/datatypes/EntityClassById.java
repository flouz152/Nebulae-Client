package beame.components.baritone.api.command.datatypes;

import beame.components.baritone.api.command.exception.CommandException;
import beame.components.baritone.api.command.helpers.TabCompleteHelper;
import net.minecraft.entity.EntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;

import java.util.stream.Stream;

public enum EntityClassById implements IDatatypeFor<EntityType> {
// leaked by itskekoff; discord.gg/sk3d 20VA2VLp
    INSTANCE;

    @Override
    public EntityType get(IDatatypeContext ctx) throws CommandException {
        ResourceLocation id = new ResourceLocation(ctx.getConsumer().getString());
        EntityType entity;
        if ((entity = Registry.ENTITY_TYPE.getOptional(id).orElse(null)) == null) {
            throw new IllegalArgumentException("no entity found by that id");
        }
        return entity;
    }

    @Override
    public Stream<String> tabComplete(IDatatypeContext ctx) throws CommandException {
        return new TabCompleteHelper()
                .append(Registry.ENTITY_TYPE.stream().map(Object::toString))
                .filterPrefixNamespaced(ctx.getConsumer().getString())
                .sortAlphabetically()
                .stream();
    }
}
