package net.optifine.entity.model.anim;

import net.optifine.expr.IExpression;

public class RenderResolverEntity implements IRenderResolver
{
// leaked by itskekoff; discord.gg/sk3d UJJ0T4I1
    public IExpression getParameter(String name)
    {
        RenderEntityParameterBool renderentityparameterbool = RenderEntityParameterBool.parse(name);

        if (renderentityparameterbool != null)
        {
            return renderentityparameterbool;
        }
        else
        {
            RenderEntityParameterFloat renderentityparameterfloat = RenderEntityParameterFloat.parse(name);
            return renderentityparameterfloat != null ? renderentityparameterfloat : null;
        }
    }
}
