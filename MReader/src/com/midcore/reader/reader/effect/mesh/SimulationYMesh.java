package com.midcore.reader.reader.effect.mesh;

import com.glview.graphics.mesh.BasicMesh;
import com.glview.libgdx.graphics.VertexAttribute;
import com.glview.libgdx.graphics.VertexAttributes.Usage;
import com.glview.libgdx.graphics.glutils.ShaderProgram;
import com.glview.libgdx.graphics.opengl.GL20;

//axis
/*                             |
 *                             |
 *                        1          3
 *           -----------------------------------
 *                        2          4
 * 							   |
 * 							   |
 * index: 1 3 2 2 3 4
 * 
 */
public class SimulationYMesh extends BasicMesh {
	
	private final static int WIDTH_MESH_COUNT = 50;
	private final static int HEIGHT_MESH_COUNT = 1;
	
	float mWidth, mHeight;
	float mRadius;
	float mPos1, mPos2;
	
	float mRightShadowPosition;
	float mLeftShadowPosition;
	
	float[] vertices;
	short[] indices;
	
	public SimulationYMesh(float w, float h) {
		super(GL20.GL_TRIANGLES, new VertexAttribute(Usage.Position, 2, ShaderProgram.POSITION_ATTRIBUTE), 
				new VertexAttribute(Usage.TextureCoordinates, 2, ShaderProgram.TEXCOORD_ATTRIBUTE));
		mWidth = w;
		mHeight = h;
		mRadius = mWidth / 8;
		mPos1 = (float) (Math.PI * mRadius / 2 - mRadius);
		mPos2 = (float) (Math.PI * mRadius);
		
		vertices = new float[(WIDTH_MESH_COUNT + 1) * (HEIGHT_MESH_COUNT + 1) * 4];
		indices = new short[WIDTH_MESH_COUNT * HEIGHT_MESH_COUNT * 2 * 3];
		
		initialMeshXYUV();
		initialIndices();

		setVertexCount(vertices.length / 4);
		setIndexCount(indices.length);
	}
	
	@Override
	public short[] generateIndices() {
		return indices;
	}
	
	@Override
	public float[] generateVertices() {
		return vertices;
	}
	
	private void initialMeshXYUV() {
		float dx, dy;
		int loop = 0;
		for (int x = 0; x <= WIDTH_MESH_COUNT; x ++) {
			dx = x * mWidth / WIDTH_MESH_COUNT;
			for (int y = 0; y <= HEIGHT_MESH_COUNT; y ++) {
				dy = y * mHeight / HEIGHT_MESH_COUNT;
				vertices[loop] = dx;
				vertices[loop + 1] = dy;
				vertices[loop + 2] = dx /mWidth;
				vertices[loop + 3] = dy / mHeight;
				loop += 4;
			}
		}
	}
	
	public float getRightShadowPosition() {
		return mRightShadowPosition;
	}
	
	public float getLeftShadowPosition() {
		return mLeftShadowPosition;
	}
	
	public void setCurrentPosition(float position) {
		mLeftShadowPosition = position;
		float p = mWidth - position;
		float tiltPosition = 0;
		
		if (p < mPos2) {
			tiltPosition = mWidth - p;
			float d = mWidth - tiltPosition;
			mLeftShadowPosition = (float) (tiltPosition + Math.sin(d / mRadius) * mRadius);
			if (d > Math.PI * mRadius / 2) {
				mRightShadowPosition = (float) (tiltPosition + mRadius);
			} else {
				mRightShadowPosition = mLeftShadowPosition;
			}
		} else {
			// w - p + (w - tp - PI * R) = tp
			tiltPosition = (float) ((mWidth - p + mWidth - Math.PI * mRadius) / 2);
			mRightShadowPosition = (float) (tiltPosition + mRadius);
		}
		
		float dx, dy;
		int loop = 0;
		for (int x = 0; x <= WIDTH_MESH_COUNT; x ++) {
			dx = x * mWidth / WIDTH_MESH_COUNT;
			if (dx > tiltPosition) {
				float d = dx - tiltPosition;
				if (d > Math.PI * mRadius) {
					dx = (float) (tiltPosition - (d - Math.PI * mRadius));
				} else {
					dx = (float) (tiltPosition + Math.sin(d / mRadius) * mRadius);
				}
			}
			for (int y = 0; y <= HEIGHT_MESH_COUNT; y ++) {
				dy = y * mHeight / HEIGHT_MESH_COUNT;
				vertices[loop] = dx;
				vertices[loop + 1] = dy;
				loop += 4;
			}
		}
	}
	
	private void initialIndices() {
		int loop = 0;
		for (int x = 0; x < WIDTH_MESH_COUNT; x ++) {
			for (int y = 0; y < HEIGHT_MESH_COUNT; y ++) {
				indices[loop] = (short) (x * (HEIGHT_MESH_COUNT + 1) + y);
				indices[loop + 2] = (short) (x * (HEIGHT_MESH_COUNT + 1) + y + HEIGHT_MESH_COUNT + 1);
				indices[loop + 1] = (short) (x * (HEIGHT_MESH_COUNT + 1) + y + 1);
				indices[loop + 4] = (short) (x * (HEIGHT_MESH_COUNT + 1) + y + 1);
				indices[loop + 3] = (short) (x * (HEIGHT_MESH_COUNT + 1) + y + HEIGHT_MESH_COUNT + 1);
				indices[loop + 5] = (short) (x * (HEIGHT_MESH_COUNT + 1) + y + 1 + HEIGHT_MESH_COUNT + 1);
				loop += 6;
			}
		}
	}
	
}
