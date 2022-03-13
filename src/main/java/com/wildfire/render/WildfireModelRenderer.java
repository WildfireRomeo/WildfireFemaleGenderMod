package com.wildfire.render;
/*
Wildfire's Female Gender Mod is a female gender mod created for Minecraft.
Copyright (C) 2022  WildfireRomeo

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3f;

public class WildfireModelRenderer {

	public static class ModelBox {
		public final WildfireModelRenderer.TexturedQuad[] quads;
		public final float posX1;
		public final float posY1;
		public final float posZ1;
		public final float posX2;
		public final float posY2;
		public final float posZ2;

		public ModelBox(int tW, int tH, int texU, int texV, float x, float y, float z, int dx, int dy, int dz, float delta, boolean mirror) {
			this.posX1 = x;
			this.posY1 = y;
			this.posZ1 = z;
			this.posX2 = x + (float)dx;
			this.posY2 = y + (float)dy;
			this.posZ2 = z + (float)dz;
			this.quads = new TexturedQuad[5];
			float f = x + (float)dx;
			float f1 = y + (float)dy;
			float f2 = z + (float)dz;
			x = x - delta;
			y = y - delta;
			z = z - delta;
			f = f + delta;
			f1 = f1 + delta;
			f2 = f2 + delta;
			if (mirror) {
				float f3 = f;
				f = x;
				x = f3;
			}

			WildfireModelRenderer.PositionTextureVertex positiontexturevertex7 = new WildfireModelRenderer.PositionTextureVertex(x, y, z, 0.0F, 0.0F);
			WildfireModelRenderer.PositionTextureVertex positiontexturevertex = new WildfireModelRenderer.PositionTextureVertex(f, y, z, 0.0F, 8.0F);
			WildfireModelRenderer.PositionTextureVertex positiontexturevertex1 = new WildfireModelRenderer.PositionTextureVertex(f, f1, z, 8.0F, 8.0F);
			WildfireModelRenderer.PositionTextureVertex positiontexturevertex2 = new WildfireModelRenderer.PositionTextureVertex(x, f1, z, 8.0F, 0.0F);
			WildfireModelRenderer.PositionTextureVertex positiontexturevertex3 = new WildfireModelRenderer.PositionTextureVertex(x, y, f2, 0.0F, 0.0F);
			WildfireModelRenderer.PositionTextureVertex positiontexturevertex4 = new WildfireModelRenderer.PositionTextureVertex(f, y, f2, 0.0F, 8.0F);
			WildfireModelRenderer.PositionTextureVertex positiontexturevertex5 = new WildfireModelRenderer.PositionTextureVertex(f, f1, f2, 8.0F, 8.0F);
			WildfireModelRenderer.PositionTextureVertex positiontexturevertex6 = new WildfireModelRenderer.PositionTextureVertex(x, f1, f2, 8.0F, 0.0F);

			this.quads[0] = new TexturedQuad(new PositionTextureVertex[]{positiontexturevertex4, positiontexturevertex, positiontexturevertex1, positiontexturevertex5}, texU + dz + dx, texV + dz, texU + dz + dx + dz, texV + dz + dy, tW, tH, mirror, Direction.EAST);
			this.quads[1] = new TexturedQuad(new PositionTextureVertex[]{positiontexturevertex7, positiontexturevertex3, positiontexturevertex6, positiontexturevertex2}, texU, texV + dz, texU + dz, texV + dz + dy, tW, tH, mirror, Direction.WEST);
			this.quads[2] = new TexturedQuad(new PositionTextureVertex[]{positiontexturevertex4, positiontexturevertex3, positiontexturevertex7, positiontexturevertex}, texU + dz, texV, texU + dz + dx, texV + dz, tW, tH, mirror, Direction.DOWN);
			this.quads[3] = new TexturedQuad(new PositionTextureVertex[]{positiontexturevertex1, positiontexturevertex2, positiontexturevertex6, positiontexturevertex5}, texU + dz, texV + dz + 4, texU + dz + dx, texV + 1 + dz + dy, tW, tH-1, mirror, Direction.UP);
			this.quads[4] = new TexturedQuad(new PositionTextureVertex[]{positiontexturevertex, positiontexturevertex7, positiontexturevertex2, positiontexturevertex1}, texU + dz, texV + dz, texU + dz + dx, texV + dz + dy, tW, tH, mirror, Direction.NORTH);
		}
	}

	public static class OverlayModelBox {
		public final WildfireModelRenderer.TexturedQuad[] quads;
		public final float posX1;
		public final float posY1;
		public final float posZ1;
		public final float posX2;
		public final float posY2;
		public final float posZ2;

		public OverlayModelBox(boolean isLeft, int tW, int tH, int texU, int texV, float x, float y, float z, int dx, int dy, int dz, float delta, boolean mirror) {
			this.posX1 = x;
			this.posY1 = y;
			this.posZ1 = z;
			this.posX2 = x + (float)dx;
			this.posY2 = y + (float)dy;
			this.posZ2 = z + (float)dz;
			this.quads = new TexturedQuad[4];
			float f = x + (float)dx;
			float f1 = y + (float)dy;
			float f2 = z + (float)dz;
			x = x - delta;
			y = y - delta;
			z = z - delta;
			f = f + delta;
			f1 = f1 + delta;
			f2 = f2 + delta;
			if (mirror) {
				float f3 = f;
				f = x;
				x = f3;
			}

			WildfireModelRenderer.PositionTextureVertex positiontexturevertex7 = new WildfireModelRenderer.PositionTextureVertex(x, y, z, 0.0F, 0.0F);
			WildfireModelRenderer.PositionTextureVertex positiontexturevertex = new WildfireModelRenderer.PositionTextureVertex(f, y, z, 0.0F, 8.0F);
			WildfireModelRenderer.PositionTextureVertex positiontexturevertex1 = new WildfireModelRenderer.PositionTextureVertex(f, f1, z, 8.0F, 8.0F);
			WildfireModelRenderer.PositionTextureVertex positiontexturevertex2 = new WildfireModelRenderer.PositionTextureVertex(x, f1, z, 8.0F, 0.0F);
			WildfireModelRenderer.PositionTextureVertex positiontexturevertex3 = new WildfireModelRenderer.PositionTextureVertex(x, y, f2, 0.0F, 0.0F);
			WildfireModelRenderer.PositionTextureVertex positiontexturevertex4 = new WildfireModelRenderer.PositionTextureVertex(f, y, f2, 0.0F, 8.0F);
			WildfireModelRenderer.PositionTextureVertex positiontexturevertex5 = new WildfireModelRenderer.PositionTextureVertex(f, f1, f2, 8.0F, 8.0F);
			WildfireModelRenderer.PositionTextureVertex positiontexturevertex6 = new WildfireModelRenderer.PositionTextureVertex(x, f1, f2, 8.0F, 0.0F);

			if(!isLeft) {
				this.quads[0] = new TexturedQuad(new PositionTextureVertex[]{positiontexturevertex4, positiontexturevertex, positiontexturevertex1, positiontexturevertex5}, texU + dz + dx, texV + dz, texU + dz + dx + dz, texV + dz + dy, tW, tH, mirror, Direction.EAST);
			} else {
				this.quads[0] = new TexturedQuad(new PositionTextureVertex[]{positiontexturevertex7, positiontexturevertex3, positiontexturevertex6, positiontexturevertex2}, texU, texV + dz, texU + dz, texV + dz + dy, tW, tH, mirror, Direction.WEST);
			}
			this.quads[1] = new TexturedQuad(new PositionTextureVertex[]{positiontexturevertex4, positiontexturevertex3, positiontexturevertex7, positiontexturevertex}, texU + dz, texV, texU + dz + dx, texV + dz, tW, tH, mirror, Direction.DOWN);
			this.quads[2] = new TexturedQuad(new PositionTextureVertex[]{positiontexturevertex1, positiontexturevertex2, positiontexturevertex6, positiontexturevertex5}, texU + dz, texV + dz + 4, texU + dz + dx, texV + 1 + dz + dy, tW, tH-1, mirror, Direction.UP);
			this.quads[3] = new TexturedQuad(new PositionTextureVertex[]{positiontexturevertex, positiontexturevertex7, positiontexturevertex2, positiontexturevertex1}, texU + dz, texV + dz, texU + dz + dx, texV + dz + dy, tW, tH, mirror, Direction.NORTH);
		}
	}



	public static class BreastModelBox {
		public final WildfireModelRenderer.TexturedQuad[] quads;
		public final float posX1;
		public final float posY1;
		public final float posZ1;
		public final float posX2;
		public final float posY2;
		public final float posZ2;

		public BreastModelBox(int tW, int tH, int texU, int texV, float x, float y, float z, int dx, int dy, int dz, float delta, boolean mirror) {
			this.posX1 = x;
			this.posY1 = y;
			this.posZ1 = z;
			this.posX2 = x + (float)dx;
			this.posY2 = y + (float)dy;
			this.posZ2 = z + (float)dz;
			this.quads = new TexturedQuad[5];
			float f = x + (float)dx;
			float f1 = y + (float)dy;
			float f2 = z + (float)dz;
			x = x - delta;
			y = y - delta;
			z = z - delta;
			f = f + delta;
			f1 = f1 + delta;
			f2 = f2 + delta;
			if (mirror) {
				float f3 = f;
				f = x;
				x = f3;
			}

			WildfireModelRenderer.PositionTextureVertex positiontexturevertex7 = new WildfireModelRenderer.PositionTextureVertex(x, y, z, 0.0F, 0.0F);
			WildfireModelRenderer.PositionTextureVertex positiontexturevertex = new WildfireModelRenderer.PositionTextureVertex(f, y, z, 0.0F, 8.0F);
			WildfireModelRenderer.PositionTextureVertex positiontexturevertex1 = new WildfireModelRenderer.PositionTextureVertex(f, f1, z, 8.0F, 8.0F);
			WildfireModelRenderer.PositionTextureVertex positiontexturevertex2 = new WildfireModelRenderer.PositionTextureVertex(x, f1, z, 8.0F, 0.0F);
			WildfireModelRenderer.PositionTextureVertex positiontexturevertex3 = new WildfireModelRenderer.PositionTextureVertex(x, y, f2, 0.0F, 0.0F);
			WildfireModelRenderer.PositionTextureVertex positiontexturevertex4 = new WildfireModelRenderer.PositionTextureVertex(f, y, f2, 0.0F, 8.0F);
			WildfireModelRenderer.PositionTextureVertex positiontexturevertex5 = new WildfireModelRenderer.PositionTextureVertex(f, f1, f2, 8.0F, 8.0F);
			WildfireModelRenderer.PositionTextureVertex positiontexturevertex6 = new WildfireModelRenderer.PositionTextureVertex(x, f1, f2, 8.0F, 0.0F);

			this.quads[0] = new TexturedQuad(new PositionTextureVertex[]{positiontexturevertex4, positiontexturevertex, positiontexturevertex1, positiontexturevertex5}, texU + 4 + dx, texV + 4, texU + 4 + dx + 4, texV + 4 + dy, tW, tH, mirror, Direction.EAST);
			this.quads[1] = new TexturedQuad(new PositionTextureVertex[]{positiontexturevertex7, positiontexturevertex3, positiontexturevertex6, positiontexturevertex2}, texU, texV + 4, texU + 4, texV + 4 + dy, tW, tH, mirror, Direction.WEST);
			this.quads[2] = new TexturedQuad(new PositionTextureVertex[]{positiontexturevertex4, positiontexturevertex3, positiontexturevertex7, positiontexturevertex}, texU + 4, texV, texU + 4 + dx, texV + 4, tW, tH, mirror, Direction.DOWN);
			this.quads[3] = new TexturedQuad(new PositionTextureVertex[]{positiontexturevertex1, positiontexturevertex2, positiontexturevertex6, positiontexturevertex5}, texU + 4, texV + 4 + 4, texU + 4 + dx, texV + 1 + 4 + dy, tW, tH-1, mirror, Direction.UP);
			this.quads[4] = new TexturedQuad(new PositionTextureVertex[]{positiontexturevertex, positiontexturevertex7, positiontexturevertex2, positiontexturevertex1}, texU + 4, texV + 4, texU + 4 + dx, texV + 4 + dy, tW, tH, mirror, Direction.NORTH);
		}
	}

	   public static class SkinnedModelBox {
	      public final WildfireModelRenderer.TexturedQuad[] quads;
	      public final float posX1;
	      public final float posY1;
	      public final float posZ1;
	      public final float posX2;
	      public final float posY2;
	      public final float posZ2;

	      public SkinnedModelBox(int tW, int tH, int texU, int texV, float x, float y, float z, int dx, int dy, int dz, float delta, boolean mirror) {
	    	  this.posX1 = x;
	          this.posY1 = y;
	          this.posZ1 = z;
	          this.posX2 = x + (float)dx;
	          this.posY2 = y + (float)dy;
	          this.posZ2 = z + (float)dz;
	          this.quads = new TexturedQuad[6];
	          float f = x + (float)dx;
	          float f1 = y + (float)dy;
	          float f2 = z + (float)dz;
	          x = x - delta;
	          y = y - delta;
	          z = z - delta;
	          f = f + delta;
	          f1 = f1 + delta;
	          f2 = f2 + delta;
	          if (mirror) {
	             float f3 = f;
	             f = x;
	             x = f3;
	          }

	         WildfireModelRenderer.PositionTextureVertex positiontexturevertex7 = new WildfireModelRenderer.PositionTextureVertex(x, y, z, 0.0F, 0.0F);
	         WildfireModelRenderer.PositionTextureVertex positiontexturevertex = new WildfireModelRenderer.PositionTextureVertex(f, y, z, 0.0F, 8.0F);
	         WildfireModelRenderer.PositionTextureVertex positiontexturevertex1 = new WildfireModelRenderer.PositionTextureVertex(f, f1, z, 8.0F, 8.0F);
	         WildfireModelRenderer.PositionTextureVertex positiontexturevertex2 = new WildfireModelRenderer.PositionTextureVertex(x, f1, z, 8.0F, 0.0F);
	         WildfireModelRenderer.PositionTextureVertex positiontexturevertex3 = new WildfireModelRenderer.PositionTextureVertex(x, y, f2, 0.0F, 0.0F);
	         WildfireModelRenderer.PositionTextureVertex positiontexturevertex4 = new WildfireModelRenderer.PositionTextureVertex(f, y, f2, 0.0F, 8.0F);
	         WildfireModelRenderer.PositionTextureVertex positiontexturevertex5 = new WildfireModelRenderer.PositionTextureVertex(f, f1, f2, 8.0F, 8.0F);
	         WildfireModelRenderer.PositionTextureVertex positiontexturevertex6 = new WildfireModelRenderer.PositionTextureVertex(x, f1, f2, 8.0F, 0.0F);
	         
	         this.quads[0] = new TexturedQuad(new PositionTextureVertex[]{positiontexturevertex4, positiontexturevertex, positiontexturevertex1, positiontexturevertex5}, texU + dz + dx, texV + dz, texU + dz + dx + dz, texV + dz + dy, tW, tH, mirror, Direction.EAST);
	         this.quads[1] = new TexturedQuad(new PositionTextureVertex[]{positiontexturevertex7, positiontexturevertex3, positiontexturevertex6, positiontexturevertex2}, texU, texV + dz, texU + dz, texV + dz + dy, tW, tH, mirror, Direction.WEST);
	         this.quads[2] = new TexturedQuad(new PositionTextureVertex[]{positiontexturevertex4, positiontexturevertex3, positiontexturevertex7, positiontexturevertex}, texU + dz, texV, texU + dz + dx, texV + dz, tW, tH, mirror, Direction.DOWN);
	         this.quads[3] = new TexturedQuad(new PositionTextureVertex[]{positiontexturevertex1, positiontexturevertex2, positiontexturevertex6, positiontexturevertex5}, texU + dz + dx, texV + dz, texU + dz + dx + dx, texV, tW, tH, mirror, Direction.UP);
	         this.quads[4] = new TexturedQuad(new PositionTextureVertex[]{positiontexturevertex, positiontexturevertex7, positiontexturevertex2, positiontexturevertex1}, texU + dz, texV + dz, texU + dz + dx, texV + dz + dy, tW, tH, mirror, Direction.NORTH);
	         this.quads[5] = new TexturedQuad(new PositionTextureVertex[]{positiontexturevertex3, positiontexturevertex4, positiontexturevertex5, positiontexturevertex6}, texU + dz + dx + dz, texV + dz, texU + dz + dx + dz + dx, texV + dz + dy, tW, tH, mirror, Direction.SOUTH);
		  }
	   }


	   public static class SkinnedModelPlane {
	      public final WildfireModelRenderer.TexturedQuad[] quads;
	      public final float posX1;
	      public final float posY1;
	      public final float posZ1;
	      public final float posX2;
	      public final float posZ2;

	      public SkinnedModelPlane(int tW, int tH, int texU, int texV, float x, float y, float z, int dx, int dz, float delta, boolean mirror) {
	    	  this.posX1 = x;
	          this.posY1 = y;
	          this.posZ1 = z;
	          this.posX2 = x + (float)dx;
	          this.posZ2 = z + (float)dz;
	          this.quads = new TexturedQuad[1];
	          float f = x + (float)dx;
	          float f2 = z + (float)dz;
	          x = x - delta;
	          y = y - delta;
	          z = z - delta;
	          f = f + delta;
	          f2 = f2 + delta;
	          if (mirror) {
	             float f3 = f;
	             f = x;
	             x = f3;
	          }

	          PositionTextureVertex positiontexturevertex7 = new PositionTextureVertex(x, y, z, 0.0F, 0.0F);
	          PositionTextureVertex positiontexturevertex = new PositionTextureVertex(f, y, z, 0.0F, 8.0F);
	          PositionTextureVertex positiontexturevertex3 = new PositionTextureVertex(x, y, f2, 0.0F, 0.0F);
	          PositionTextureVertex positiontexturevertex4 = new PositionTextureVertex(f, y, f2, 0.0F, 8.0F);
	          
	         this.quads[0] = new TexturedQuad(new PositionTextureVertex[]{positiontexturevertex4, positiontexturevertex3, positiontexturevertex7, positiontexturevertex}, texU, texV, texU + dz, texV + dz, tW, tH, mirror, Direction.EAST);
	         
	      }
	   }

   public static class PositionTextureVertex {
      public final Vec3f vector3D;
      public final float texturePositionX;
      public final float texturePositionY;

      public PositionTextureVertex(float x, float y, float z, float texU, float texV) {
         this(new Vec3f(x, y, z), texU, texV);
      }

      public WildfireModelRenderer.PositionTextureVertex setTexturePosition(float texU, float texV) {
         return new WildfireModelRenderer.PositionTextureVertex(this.vector3D, texU, texV);
      }

      public PositionTextureVertex(Vec3f posIn, float texU, float texV) {
         this.vector3D = posIn;
         this.texturePositionX = texU;
         this.texturePositionY = texV;
      }
   }

   public static class TexturedQuad {
      public final WildfireModelRenderer.PositionTextureVertex[] vertexPositions;
      public final Vec3f normal;

      public TexturedQuad(WildfireModelRenderer.PositionTextureVertex[] positionsIn, float u1, float v1, float u2, float v2, float texWidth, float texHeight, boolean mirrorIn, Direction directionIn) {
         this.vertexPositions = positionsIn;
         float f = 0.0F / texWidth;
         float f1 = 0.0F / texHeight;
         positionsIn[0] = positionsIn[0].setTexturePosition(u2 / texWidth - f, v1 / texHeight + f1);
         positionsIn[1] = positionsIn[1].setTexturePosition(u1 / texWidth + f, v1 / texHeight + f1);
         positionsIn[2] = positionsIn[2].setTexturePosition(u1 / texWidth + f, v2 / texHeight - f1);
         positionsIn[3] = positionsIn[3].setTexturePosition(u2 / texWidth - f, v2 / texHeight - f1);
         if (mirrorIn) {
            int i = positionsIn.length;

            for(int j = 0; j < i / 2; ++j) {
            	WildfireModelRenderer.PositionTextureVertex modelrenderer$positiontexturevertex = positionsIn[j];
               positionsIn[j] = positionsIn[i - 1 - j];
               positionsIn[i - 1 - j] = modelrenderer$positiontexturevertex;
            }
         }

         this.normal = directionIn.getUnitVector();
         if (mirrorIn) {
            this.normal.multiplyComponentwise(-1.0F, 1.0F, 1.0F);
         }

      }
   }
}