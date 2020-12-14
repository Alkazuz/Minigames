package br.alkazuz.antiaura.utils;

public class MathHelper {

	public static float sqrt_double(double p_76133_0_)
    {
        return (float)Math.sqrt(p_76133_0_);
    }
	
	/**
     * the angle is reduced to an angle between -180 and +180 by mod, and a 360 check
     */
    public static float wrapAngleTo180_float(float p_76142_0_)
    {
        p_76142_0_ %= 360.0F;

        if (p_76142_0_ >= 180.0F)
        {
            p_76142_0_ -= 360.0F;
        }

        if (p_76142_0_ < -180.0F)
        {
            p_76142_0_ += 360.0F;
        }

        return p_76142_0_;
    }
	
}
