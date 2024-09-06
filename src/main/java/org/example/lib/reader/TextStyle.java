package org.example.lib.reader;

import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.graphics.color.PDColor;

import java.util.Objects;

public class TextStyle {
	private float textHeight;
	private PDFont font;
	private int rotation;
	private PDColor color;
	private float direction;

	public TextStyle(float textHeight, PDFont font, int rotation, float direction, PDColor rgb) {
		this.textHeight = textHeight;
		this.font = font;
		this.rotation = rotation;
		this.color = rgb;
		this.direction = direction;
	}
	public float getTextHeight() {
		return textHeight;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null || getClass() != obj.getClass()) {
			return false;
		}
		TextStyle other = (TextStyle) obj;
		return Float.compare(other.textHeight, textHeight) == 0 &&
				Objects.equals(other.font, font) &&
				other.rotation == rotation &&
				Objects.equals(other.color, color) &&
				other.direction == direction;
	}

	@Override
	public int hashCode() {
		return Objects.hash(textHeight, font, rotation, direction ,color);
	}

	public void setTextHeight(float textHeight) {
		this.textHeight = textHeight;
	}

	public PDFont getFont() {
		return font;
	}

	public void setFont(PDFont font) {
		this.font = font;
	}

	public int getRotation() {
		return rotation;
	}
	public PDColor getColor() {
		return color;
	}
	public void setRotation(int rotation) {
		this.rotation = rotation;
	}

	public float getDirection() {
		return this.getDirection();
	}

	public void setDirection(float direction) {
		this.direction = direction;
	}

}
