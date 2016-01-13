package de.jjco.graphics;

import java.awt.Color;
import java.awt.FontFormatException;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import org.lwjgl.opengl.GL11;

import de.jjco.EngineLog;
import de.jjco.resources.FileSystem;
import de.jjco.resources.GLResource;

/**
 * The OpenGL backend of the font.  This class loads each glyph into video memory for rendering
 * later.
 * 
 * @author Jared Jonas (bluesun212)
 * @version Revision 1
 */
public class Font extends GLResource {
	private static char[] chrs = new char[256];
	static {
		for (int i = 0; i < chrs.length; i++) {
			chrs[i] = (char) i;
		}
	}
	
	private int width;
	private int height;
	private int ha = -1;
	private int va = -1;
	private int[][] coords;
	private int id;

	private java.awt.Font font;
	private boolean antiAlias;
	private ByteBuffer buffer;

	public static final int ALIGN_LEFT = -1;
	public static final int ALIGN_CENTER = 0;
	public static final int ALIGN_RIGHT = 1;
	public static final int ALIGN_TOP = -1;
	public static final int ALIGN_BOTTOM = 1;

	/**
	 * Creates a font based on a pre-existing font.
	 * 
	 * @param name font name
	 * @param size point size
	 * @param aa whether to anti-alias or not
	 * @return the Font object
	 */
	public static Font createSystemFont(String name, int size, boolean aa) {
		java.awt.Font f = new java.awt.Font(name, 0, size);
		return ( new Font(f, aa) );
	}

	/**
	 * Creates a Font based on a TrueType resource.
	 * 
	 * @param resource the resource
	 * @param size point size
	 * @param aa whether to anti-alias or not
	 * @return the Font object
	 */
	public static Font loadFont(String resource, int size, boolean aa) {
		InputStream is = FileSystem.getInputStream(resource);

		try {
			java.awt.Font f = java.awt.Font.createFont(java.awt.Font.TRUETYPE_FONT, is);
			f = f.deriveFont((float) size);
			return ( new Font(f, aa) );
		} catch (FontFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return ( null );
	}

	/**
	 * @return the horizontal alignment
	 */
	public int getHAlign() {
		return ha;
	}

	/**
	 * @return the vertical alignment
	 */
	public int getVAlign() {
		return va;
	}

	/**
	 * @param ha the font's horizontal alignment
	 * @param va the font's vertical alignment
	 */
	public void setAlign(int ha, int va) {
		this.ha = ha;
		this.va = va;
	}

	/**
	 * Creates a TrueTypeFont from the font object.  This is called from the
	 * FontLoader class.  Please do not call this function as it throws errors
	 * outside of the rendering thread.
	 * 
	 * @param font the AWT font
	 * @param antiAlias if the font should be anti aliased 
	 */
	public Font(java.awt.Font font, boolean antiAlias) {
		this.font = font;
		this.antiAlias = antiAlias;
	}

	/**
	 * Gets the width of the text rendered in this font.
	 * 
	 * @param s the text
	 * @return the width of the text
	 */
	public int getWidth(String s) {
		if (!isLoaded()) {
			return ( 0 );
		}

		double w = 0;

		for (int i = 0; i < s.length(); i++) {
			int c = s.charAt(i);
			if(c <= 256) {
				w += coords[c][6];
			}
		}

		return ((int) w);
	}

	/**
	 * Gets the font's height
	 * 
	 * @return the font height
	 */
	public int getHeight() {
		return ( height / 16 );
	}

	/**
	 * Draws text onto the screen.
	 * 
	 * @param x the x coordinate
	 * @param y the y coordinate
	 * @param s the text
	 */
	public void drawString(float x, float y, String s) { // TODO update to use new openGL
		if (!isLoaded() || s == null) {
			return;
		}

		int w = getWidth(s);
		int h = getHeight();
		y += h;
		x -= w * (ha + 1) / 2;
		y -= h * (va + 1) / 2;

		GL11.glPushMatrix();
		GL11.glTranslated(x, y, 0);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, id);
		GL11.glBegin(GL11.GL_QUADS);

		double pos = 0;
		for ( int i = 0; i < s.length(); i++ ) {
			int c = s.charAt(i);
			if (c < 256) {
				double u1 = coords[c][0] / (double)width;
				double v1 = coords[c][1] / (double)height;
				double u2 = (coords[c][0] + coords[c][2]) / (double)width;
				double v2 = (coords[c][1] + coords[c][3]) / (double)height;
				double x1 = pos + coords[c][4];
				double y1 = coords[c][5];
				double x2 = x1 + coords[c][2];
				double y2 = y1 + coords[c][3];

				GL11.glTexCoord2d(u1, v1);
				GL11.glVertex2d(x1, y1);
				GL11.glTexCoord2d(u2, v1);
				GL11.glVertex2d(x2, y1);
				GL11.glTexCoord2d(u2, v2);
				GL11.glVertex2d(x2, y2);
				GL11.glTexCoord2d(u1, v2);
				GL11.glVertex2d(x1, y2);
				pos += coords[c][6];
			}
		}

		GL11.glEnd();
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glPopMatrix();
	}

	@Override
	protected void doLoad() {
		// First, create a temporary buffered image to get the font metrics
		BufferedImage bi = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
		Graphics2D temp = (Graphics2D) bi.getGraphics();
		temp.setFont(font);

		FontMetrics metrics = temp.getFontMetrics();
		width = metrics.getMaxAdvance() * 16;
		height = metrics.getHeight() * 16;
		temp.dispose();

		// Create font image
		BufferedImage page = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		Graphics2D gfx = (Graphics2D) page.getGraphics();
		gfx.setColor(Color.WHITE);
		gfx.setFont(font);

		if (antiAlias) {
			gfx.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		}

		// Draw texture
		coords = new int[256][];

		GlyphVector glyphs = font.createGlyphVector(gfx.getFontRenderContext(), chrs);
		int cmh = 0;
		int cx = 0;
		int cy = 0;

		for (int i = 0; i < chrs.length; i++) {
			// Calculate bounds
			Rectangle bounds = glyphs.getGlyphPixelBounds(i, null, 0, 0);
			Point2D origin = glyphs.getGlyphPosition(i);
			bounds.translate(-(int)origin.getX(), -(int)origin.getY());

			int cw = (int) bounds.getWidth();
			int ch = (int) bounds.getHeight();
			int cxo = (int) bounds.getX();
			int cyo = (int) bounds.getY();
			int lcw = (int) gfx.getFontMetrics().getStringBounds("" + chrs[i], gfx).getWidth();
			if (ch > cmh) {
				cmh = ch;
			}

			coords[i] = new int[]{cx, cy, cw, ch, cxo, cyo, lcw};

			// Draw
			AffineTransform id = gfx.getTransform();
			gfx.translate(cx-cxo, cy-cyo);
			gfx.drawString("" + chrs[i], 0, 0);
			gfx.setTransform(id);

			// Move
			cx += cw + 2;
			if (i != 0 && i % 16 == 0) {
				cx = 0;
				cy += cmh + 2;
				cmh = 0;
			}
		}

		// Finally, make the image into an openGL compatible texture
		int[] data = ((DataBufferInt) page.getRaster().getDataBuffer()).getData();
		buffer = ByteBuffer.allocateDirect(width * height * 4);

		for ( int i = 0; i < data.length; i++ ) {
			buffer.put((byte) ((data[i] >> 16) & 0xff));
			buffer.put((byte) ((data[i] >> 8) & 0xff));
			buffer.put((byte) (data[i] & 0xff));
			buffer.put((byte) ((data[i] >> 24) & 0xff));
		}

		gfx.dispose();
		buffer.rewind();
	}

	@Override
	protected void doLoadWithGL() {
		id = GL11.glGenTextures();
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, id);
		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, width, height, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
		EngineLog.log("Loaded " + font.getFontName() + ":" + height + " into ID " + id);
	}

	@Override
	protected void doDestroyWithGL() {
		GL11.glDeleteTextures(id);
	}

	protected void doDestroy() {}
}