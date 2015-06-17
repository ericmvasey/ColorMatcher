package apotheosis.colormatcher;

import android.graphics.Color;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 *  The HSLColor class provides methods to manipulate HSL (Hue, Saturation
 *  Luminance) values to create a corresponding Color object using the RGB
 *  ColorSpace.
 *
 *  The HUE is the color, the Saturation is the purity of the color (with
 *  respect to grey) and Luminance is the brightness of the color (with respect
 *  to black and white)
 *
 *  The Hue is specified as an angel between 0 - 360 degrees where red is 0,
 *  green is 120 and blue is 240. In between you have the colors of the rainbow.
 *  Saturation is specified as a percentage between 0 - 100 where 100 is fully
 *  saturated and 0 approaches gray. Luminance is specified as a percentage
 *  between 0 - 100 where 0 is black and 100 is white.
 *
 *  In particular the HSL color space makes it easier change the Tone or Shade
 *  of a color by adjusting the luminance value.
 *
 *  From http://tips4java.wordpress.com/2009/07/05/hsl-color/
 *  Modified by Eric Vasey
 */
@SuppressWarnings("ALL")
public class
        HSLColor
{
	private int rgb;
	private float[] hsl;
	private float alpha;

	/**
	 *  Create a HSLColor object using an RGB Color object.
	 *
	 *  @param rgb the RGB Color object
	 */
	public HSLColor(int rgb)
	{
		this.rgb = rgb;
		hsl = fromRGB( rgb );
		alpha = 1.0f;
	}

	/**
	 *  Create a HSLColor object using individual HSL values and a default
	 * alpha value of 1.0.
	 *
	 *  @param h is the Hue value in degrees between 0 - 360
	 *  @param s is the Saturation percentage between 0 - 100
	 *  @param l is the Lumanance percentage between 0 - 100
	 */
	public HSLColor(float h, float s, float l)
	{
		this(h, s, l, 1.0f);
	}

	/**
	 *  Create a HSLColor object using individual HSL values.
	 *
	 *  @param h     the Hue value in degrees between 0 - 360
	 *  @param s     the Saturation percentage between 0 - 100
	 *  @param l     the Lumanance percentage between 0 - 100
	 *  @param alpha the alpha value between 0 - 1
	 */
	public HSLColor(float h, float s, float l, float alpha)
	{
		hsl = new float[] {h, s, l};
		this.alpha = 1.0f;
		rgb = toRGB(hsl, 1.0f);
	}

	/**
	 *  Create a HSLColor object using an an array containing the
	 *  individual HSL values and with a default alpha value of 1.
	 *
	 *  @param hsl  array containing HSL values
	 */
	public HSLColor(float[] hsl)
	{
		this(hsl, 1.0f);
	}

	/**
	 *  Create a HSLColor object using an an array containing the
	 *  individual HSL values.
	 *
	 *  @param hsl  array containing HSL values
	 *  @param alpha the alpha value between 0 - 1
	 */
	public HSLColor(float[] hsl, float alpha)
	{
		this.hsl = hsl;
		this.alpha = 1.0f;
		rgb = toRGB(hsl, 1.0f);
	}

	/**
	 *  Create a RGB Color object based on this HSLColor with a different
	 *  Hue value. The degrees specified is an absolute value.
	 *
	 *  @param degrees - the Hue value between 0 - 360
	 *  @return the RGB Color object
	 */
	public int adjustHue(float degrees)
	{
		return toRGB(degrees, hsl[1], hsl[2], alpha);
	}

	/**
	 *  Create a RGB Color object based on this HSLColor with a different
	 *  Luminance value. The percent specified is an absolute value.
	 *
	 *  @param percent - the Luminance value between 0 - 100
	 *  @return the RGB Color object
	 */
	public int adjustLuminance(float percent)
	{
		return toRGB(hsl[0], hsl[1], percent, alpha);
	}

	/**
	 *  Create a RGB Color object based on this HSLColor with a different
	 *  Saturation value. The percent specified is an absolute value.
	 *
	 *  @param percent - the Saturation value between 0 - 100
	 *  @return the RGB Color object
	 */
	public int adjustSaturation(float percent)
	{
		return toRGB(hsl[0], percent, hsl[2], alpha);
	}
    public void setSaturation(float percent){ hsl[1] = percent; }

	/**
	 *  Create a RGB Color object based on this HSLColor with a different
	 *  Shade. Changing the shade will return a darker color. The percent
	 *  specified is a relative value.
	 *
	 *  @param percent - the value between 0 - 100
	 *  @return the RGB Color object
	 */
	public int adjustShade(float percent)
	{
		float multiplier = (100.0f - percent) / 100.0f;
		float l = Math.max(0.0f, hsl[2] * multiplier);

		return toRGB(hsl[0], hsl[1], l, alpha);
	}

	/**
	 *  Create a RGB Color object based on this HSLColor with a different
	 *  Tone. Changing the tone will return a lighter color. The percent
	 *  specified is a relative value.
	 *
	 *  @param percent - the value between 0 - 100
	 *  @return the RGB Color object
	 */
	public int adjustTone(float percent)
	{
		float multiplier = (100.0f + percent) / 100.0f;
		float l = Math.min(100.0f, hsl[2] * multiplier);

		return toRGB(hsl[0], hsl[1], l, alpha);
	}

	/**
	 *  Get the Alpha value.
	 *
	 *  @return the Alpha value.
	 */
	public float getAlpha()
	{
		return alpha;
	}

    private float modHue(float modAmt)
    {
        return (hsl[0] + modAmt) % 360f;
    }

	/**
	 *  Create a RGB Color object that is the complementary color of this
	 *  HSLColor. This is a convenience method. The complementary color is
	 *  determined by adding 180 degrees to the Hue value.
	 *  @return the RGB Color object
	 */
	public int getComplementary()
	{
        float hue = modHue(180.0f);
		return toRGB(hue, hsl[1], hsl[2], alpha);
	}

    public int[] getSplitComplementary()
    {
        float hue1 = modHue(150.0f),
            hue2 = modHue(210.0f);

        return new int[]{toRGB(hue1, hsl[1], hsl[2], alpha), toRGB(hue2, hsl[1], hsl[2], alpha)};
    }

    public int[] getTriadic()
    {
        float hue1 = modHue(120.0f),
                hue2 = modHue(240.0f);

        return new int[]{toRGB(hue1, hsl[1], hsl[2], alpha), toRGB(hue2, hsl[1], hsl[2], alpha)};
    }

    public int[] getTetradic()
    {
        float hue1 = modHue(90.0f),
            hue2 = modHue(180.0f),
            hue3 = modHue(270.0f);

        return new int[]
        {
                toRGB(hue1, hsl[1], hsl[2], alpha),
                toRGB(hue2, hsl[1], hsl[2], alpha),
                toRGB(hue3, hsl[1], hsl[2], alpha)
        };
    }

    public int[] getAnalgous()
    {
        float hue1 = modHue(-30.0f),
                hue2 = modHue(30.0f);

        return new int[]{toRGB(hue1, hsl[1], hsl[2], alpha), toRGB(hue2, hsl[1], hsl[2], alpha)};
    }

    public List<HSLColor> getAllHarmonies()
    {
        ArrayList<HSLColor> colors = new ArrayList<>();

        for(int color: getSplitComplementary())
        {
            colors.add(new HSLColor(color));
        }

        for(int color: getTriadic())
        {
            colors.add(new HSLColor(color));
        }

        for(int color: getTetradic())
        {
            colors.add(new HSLColor(color));
        }

        for(int color: getAnalgous())
        {
            colors.add(new HSLColor(color));
        }

        return colors;
    }

    public List<HSLColor> getClothingHarmonies()
    {
        ArrayList<HSLColor> colors = new ArrayList<>();

        for(int color: getSplitComplementary())
        {
            colors.add(new HSLColor(color));
        }

        for(int color: getTriadic())
        {
            colors.add(new HSLColor(color));
        }

        for(int color: getTetradic())
        {
            colors.add(new HSLColor(color));
        }

        for(int color: getAnalgous())
        {
            colors.add(new HSLColor(color));
        }

        return colors;
    }

    public boolean isPrimaryColor()
    {
        //Red
        if(getHue() < 20 || getHue() > 340)
        {
            Log.d("Primary Color" ,"Red");
            return true;
        }

        //Yellow
        if(getHue() < 75 && getHue() > 45 )
        {
            Log.d("Primary Color" ,"Yellow");
            return true;
        }

        //Blue
        if(getHue() < 255 && getHue() > 225)
        {
            Log.d("Primary Color" ,"Blue");
            return true;
        }

        return false;
    }

    public boolean isSecondaryColor()
    {
        if(getHue() < 50  && getHue() > 10)
        {
            Log.d("Secondary Color", "Orange");
            return true;
        }

        if(getHue() < 140 && getHue() > 100)
        {
            Log.d("Secondary Color", "Green");
            return true;
        }

        if(getHue() < 350 && getHue() > 310)
        {
            Log.d("Secondary Color", "Purple");
            return true;
        }

        return false;
    }

    public boolean isIntermediateColor()
    {
        if(!isPrimaryColor() && !isSecondaryColor())
            return true;

        return false;
    }

	/**
	 *  Get the Hue value.
	 *
	 *  @return the Hue value.
	 */
	public float getHue()
	{
		return hsl[0];
	}

	/**
	 *  Get the HSL values.
	 *
	 *  @return the HSL values.
	 */
	public float[] getHSL()
	{
		return hsl;
	}

	/**
	 *  Get the Luminance value.
	 *
	 *  @return the Luminance value.
	 */
	public float getLuminance()
	{
		return hsl[2];
	}

	/**
	 *  Get the RGB Color object represented by this HDLColor.
	 *
	 *  @return the RGB Color object.
	 */
	public int getRGB()
	{
		return rgb;
	}

	/**
	 *  Get the Saturation value.
	 *
	 *  @return the Saturation value.
	 */
	public float getSaturation()
	{
		return hsl[1];
	}

	public String toString()
	{
		String toString =
			"HSLColor[h=" + hsl[0] +
			",s=" + hsl[1] +
			",l=" + hsl[2] +
			",alpha=" + alpha + "]";

		return toString;
	}

	/**
	 *  Convert a RGB Color to it corresponding HSL values.
	 *
	 *  @return an array containing the 3 HSL values.
	 */
	public static float[] fromRGB(int color)
	{
		//  Get RGB values in the range 0 - 1
		float r = Color.red(color)/255f;
		float g = Color.green(color)/255f;
		float b = Color.blue(color)/255f;

		//	Minimum and Maximum RGB values are used in the HSL calculations

		float min = Math.min(r, Math.min(g, b));
		float max = Math.max(r, Math.max(g, b));

		//  Calculate the Hue

		float h = 0;

		if (max == min)
			h = 0;
		else if (max == r)
			h = ((60 * (g - b) / (max - min)) + 360) % 360;
		else if (max == g)
			h = (60 * (b - r) / (max - min)) + 120;
		else if (max == b)
			h = (60 * (r - g) / (max - min)) + 240;

		//  Calculate the Luminance

		float l = (max + min) / 2;

		//  Calculate the Saturation

		float s = 0;

		if (max == min)
			s = 0;
		else if (l <= .5f)
			s = (max - min) / (max + min);
		else
			s = (max - min) / (2 - max - min);

		return new float[] {h, s * 100, l * 100};
	}

	/**
	 *  Convert HSL values to a RGB Color with a default alpha value of 1.
	 *  H (Hue) is specified as degrees in the range 0 - 360.
	 *  S (Saturation) is specified as a percentage in the range 1 - 100.
	 *  L (Lumanance) is specified as a percentage in the range 1 - 100.
	 *
	 *  @param hsl an array containing the 3 HSL values
	 *
	 *  @return the RGB Color object
	 */
	public static int toRGB(float[] hsl)
	{
		return toRGB(hsl, 1.0f);
	}

	/**
	 *  Convert HSL values to a RGB Color.
	 *  H (Hue) is specified as degrees in the range 0 - 360.
	 *  S (Saturation) is specified as a percentage in the range 1 - 100.
	 *  L (Lumanance) is specified as a percentage in the range 1 - 100.
	 *
	 *  @param hsl    an array containing the 3 HSL values
	 *  @param alpha  the alpha value between 0 - 1
	 *
	 *  @return the RGB Color object
	 */
	public static int toRGB(float[] hsl, float alpha)
	{
		return toRGB(hsl[0], hsl[1], hsl[2], 1.0f);
	}

	/**
	 *  Convert HSL values to a RGB Color with a default alpha value of 1.
	 *
	 *  @param h Hue is specified as degrees in the range 0 - 360.
	 *  @param s Saturation is specified as a percentage in the range 1 - 100.
	 *  @param l Lumanance is specified as a percentage in the range 1 - 100.
	 *
	 *  @return the RGB Color object
	 */
	public static int toRGB(float h, float s, float l)
	{
		return toRGB(h, s, l, 1.0f);
	}

	/**
	 *  Convert HSL values to a RGB Color.
	 *
	 *  @param h Hue is specified as degrees in the range 0 - 360.
	 *  @param s Saturation is specified as a percentage in the range 1 - 100.
	 *  @param l Lumanance is specified as a percentage in the range 1 - 100.
	 *  @param alpha  the alpha value between 0 - 1
	 *
	 *  @return the RGB Color object
	 */
	public static int toRGB(float h, float s, float l, float alpha)
	{
        if (s <0.0f || s > 100.0f)
		{
			String message = "Color parameter outside of expected range - Saturation";
			throw new IllegalArgumentException( message );
		}

		if (l <0.0f || l > 100.0f)
		{
			String message = "Color parameter outside of expected range - Luminance";
			throw new IllegalArgumentException( message );
		}

		if (alpha <0.0f || alpha > 1.0f)
		{
			String message = "Color parameter outside of expected range - Alpha";
			throw new IllegalArgumentException( message );
		}

		//  Formula needs all values between 0 - 1.

		h = h % 360.0f;
		h /= 360f;
		s /= 100f;
		l /= 100f;

		float q = 0;

		if (l < 0.5)
			q = l * (1 + s);
		else
			q = (l + s) - (s * l);

		float p = 2 * l - q;

		float r = Math.max(0, HueToRGB(p, q, h + (1.0f / 3.0f)));
		float g = Math.max(0, HueToRGB(p, q, h));
		float b = Math.max(0, HueToRGB(p, q, h - (1.0f / 3.0f)));

		r = Math.min(r, 1.0f)*255f;
		g = Math.min(g, 1.0f)*255f;
		b = Math.min(b, 1.0f)*255f;

		return Color.argb((int)(alpha*255f),(int)r, (int)g, (int)b);
	}

	private static float HueToRGB(float p, float q, float h)
	{
		if (h < 0) h += 1;

		if (h > 1 ) h -= 1;

		if (6 * h < 1)
		{
			return p + ((q - p) * 6 * h);
		}

		if (2 * h < 1 )
		{
			return  q;
		}

		if (3 * h < 2)
		{
			return p + ( (q - p) * 6 * ((2.0f / 3.0f) - h) );
		}

   		return p;
	}

    public static List<HSLColor> getBComplements()
    {
        List<HSLColor> complements = new ArrayList<>();
        complements.add(new HSLColor(0, 85, 50));
        complements.add(new HSLColor(30, 85, 50));
        complements.add(new HSLColor(60, 85, 50));
        complements.add(new HSLColor(90, 85, 50));
        complements.add(new HSLColor(120, 85, 50));
        complements.add(new HSLColor(150, 100, 100));
        complements.add(new HSLColor(180, 85, 50));
        complements.add(new HSLColor(210, 85, 50));
        complements.add(new HSLColor(240, 85, 50));
        complements.add(new HSLColor(270, 85, 50));
        complements.add(new HSLColor(300, 85, 50));
        complements.add(new HSLColor(330, 85, 50));

        return complements;
    }

    public static List<HSLColor> getWComplements()
    {
        List<HSLColor> complements = new ArrayList<>();
        complements.add(new HSLColor(0, 85, 50));
        complements.add(new HSLColor(30, 85, 50));
        complements.add(new HSLColor(60, 85, 50));
        complements.add(new HSLColor(90, 85, 50));
        complements.add(new HSLColor(120, 85, 50));
        complements.add(new HSLColor(150, 0, 0));
        complements.add(new HSLColor(180, 85, 50));
        complements.add(new HSLColor(210, 85, 50));
        complements.add(new HSLColor(240, 85, 50));
        complements.add(new HSLColor(270, 85, 50));
        complements.add(new HSLColor(300, 85, 50));
        complements.add(new HSLColor(330, 85, 50));

        return complements;
    }

    public static List<HSLColor> getGComplements()
    {
        List<HSLColor> complements = new ArrayList<>();
        complements.add(new HSLColor(0, 85, 50));
        complements.add(new HSLColor(60, 85, 50));
        complements.add(new HSLColor(120, 85, 50));
        complements.add(new HSLColor(180, 85, 50));
        complements.add(new HSLColor(240, 85, 50));
        complements.add(new HSLColor(0, 0, 0));

        return complements;
    }
}
