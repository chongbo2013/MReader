package com.midcore.reader.reader.effect.mesh;

import com.glview.graphics.mesh.BasicMesh;
import com.glview.libgdx.graphics.Color;
import com.glview.libgdx.graphics.VertexAttribute;
import com.glview.libgdx.graphics.VertexAttributes.Usage;
import com.glview.libgdx.graphics.glutils.ShaderProgram;
import com.glview.libgdx.graphics.opengl.GL20;

/**
 * 现在的实现，翻转的时候扭曲严重。但是每片叶子较小，所以视觉效果还行。
 * 后续考虑优化，将每片叶子切成M*N的网格。
 * @author lijing.lj
 */
public class VerticalBlindsMesh extends BasicMesh {

	private final static int BLINDS_COUNT = 20;
	
	float mWidth, mHeight;
	float mItemWidth;
	
	float[] vertices;
	
	public VerticalBlindsMesh(float w, float h) {
		super(GL20.GL_TRIANGLES, new VertexAttribute(Usage.Position, 2, ShaderProgram.POSITION_ATTRIBUTE), 
				new VertexAttribute(Usage.ColorPacked, 4, ShaderProgram.COLOR_ATTRIBUTE),
				new VertexAttribute(Usage.TextureCoordinates, 2, ShaderProgram.TEXCOORD_ATTRIBUTE));
		mWidth = w;
		mHeight = h;
		mItemWidth = mWidth / BLINDS_COUNT;
		
		vertices = new float[BLINDS_COUNT * 6 * 5];
		
		initialMeshXYUV();
		setVertexCount(vertices.length / 5);
		setIndexCount(0);
	}
	
	@Override
	public float[] generateVertices() {
		return vertices;
	}
	
	private void initialMeshXYUV() {
		int x = 0;
		float white = Color.toFloatBits(1f, 1f, 1f, 1f);
		for (int i = 0; i < BLINDS_COUNT; i ++) {
			vertices[x] = mWidth * i / BLINDS_COUNT;
			vertices[x + 1] = 0;
			vertices[x + 2] = white;
			vertices[x + 3] = 1f * i / BLINDS_COUNT;
			vertices[x + 4] = 0;
			
			vertices[x + 5] = mWidth * i / BLINDS_COUNT;
			vertices[x + 6] = mHeight;
			vertices[x + 7] = white;
			vertices[x + 8] = 1f * i / BLINDS_COUNT;
			vertices[x + 9] = 1;
			
			vertices[x + 10] = mWidth * (i + 1) / BLINDS_COUNT;
			vertices[x + 11] = 0;
			vertices[x + 12] = white;
			vertices[x + 13] = 1f * (i + 1) / BLINDS_COUNT;
			vertices[x + 14] = 0;
			
			vertices[x + 15] = mWidth * (i + 1) / BLINDS_COUNT;
			vertices[x + 16] = 0;
			vertices[x + 17] = white;
			vertices[x + 18] = 1f * (i + 1) / BLINDS_COUNT;
			vertices[x + 19] = 0;
			
			vertices[x + 20] = mWidth * i / BLINDS_COUNT;
			vertices[x + 21] = mHeight;
			vertices[x + 22] = white;
			vertices[x + 23] = 1f * i / BLINDS_COUNT;
			vertices[x + 24] = 1;
			
			vertices[x + 25] = mWidth * (i + 1) / BLINDS_COUNT;
			vertices[x + 26] = mHeight;
			vertices[x + 27] = white;
			vertices[x + 28] = 1f * (i + 1) / BLINDS_COUNT;
			vertices[x + 29] = 1;
			x += 30;
		}
	}
	
	public void setCurrentPosition(float position) {
		if (position < 0) position = 0;
		if (position > 2) position = 2;
		
		boolean first = true;
		if (position > 1) {
			position = 2 - position;
			first = false;
		}
		float white = Color.toFloatBits(1f, 1f, 1f, 1f);
		float c = 0.5f + 0.5f * (1 - position);
		float ban = Color.toFloatBits(c, c, c, 1f);
		int x = 0;
		if (first) {
			for (int i = 0; i < BLINDS_COUNT; i ++) {
				vertices[x] = mWidth * i / BLINDS_COUNT + mItemWidth * position;
				vertices[x + 1] = mItemWidth * position / 2;
				vertices[x + 2] = ban;
				
				vertices[x + 5] = mWidth * i / BLINDS_COUNT + mItemWidth * position;
				vertices[x + 6] = mHeight - mItemWidth * position / 2;
				vertices[x + 7] = ban;
				
				vertices[x + 10] = mWidth * (i + 1) / BLINDS_COUNT;
				vertices[x + 11] = 0;
				vertices[x + 12] = white;
				
				vertices[x + 15] = mWidth * (i + 1) / BLINDS_COUNT;
				vertices[x + 16] = 0;
				vertices[x + 17] = white;
				
				vertices[x + 20] = mWidth * i / BLINDS_COUNT + mItemWidth * position;
				vertices[x + 21] = mHeight - mItemWidth * position / 2;
				vertices[x + 22] = ban;
				
				vertices[x + 25] = mWidth * (i + 1) / BLINDS_COUNT;
				vertices[x + 26] = mHeight;
				vertices[x + 27] = white;
				x += 30;
			}
		} else {
			for (int i = 0; i < BLINDS_COUNT; i ++) {
				vertices[x] = mWidth * i / BLINDS_COUNT;
				vertices[x + 1] = 0;
				vertices[x + 2] = white;
				
				vertices[x + 5] = mWidth * i / BLINDS_COUNT;
				vertices[x + 6] = mHeight;
				vertices[x + 7] = white;
				
				vertices[x + 10] = mWidth * i / BLINDS_COUNT + mItemWidth * (1 - position);
				vertices[x + 11] = mItemWidth * position / 2;
				vertices[x + 12] = ban;
				
				vertices[x + 15] = mWidth * i / BLINDS_COUNT + mItemWidth * (1 - position);
				vertices[x + 16] = mItemWidth * position / 2;
				vertices[x + 17] = ban;
				
				vertices[x + 20] = mWidth * i / BLINDS_COUNT;
				vertices[x + 21] = mHeight;
				vertices[x + 22] = white;
				
				vertices[x + 25] = mWidth * i / BLINDS_COUNT + mItemWidth * (1 - position);
				vertices[x + 26] = mHeight - mItemWidth * position / 2;
				vertices[x + 27] = ban;
				x += 30;
			}
		}
	}

}
