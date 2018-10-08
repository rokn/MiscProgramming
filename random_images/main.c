#include <stdio.h>
#include <stdlib.h>
#include <math.h>
#include "image.h"

struct circle_t
{
	int x, y, r;
};

static float dist(int x1, int y1, int x2, int y2)
{
	return sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
}

int main()
{
	const int w = 500;
	const int h = 500;
	const int c_count = 10;

	struct circle_t circles[c_count];
	srand(time(0));
	for (int i = 0; i < c_count; ++i) {
		circles[i].x = rand() % w;
		circles[i].y = rand() % h;
		circles[i].r = rand() % 50 + 50;
	}

	struct image_t img = init_image(w, h);

#pragma omp parallel for
	for (int y = 0; y < h; ++y) {
		for (int x = 0; x < w; ++x) {
			float d = 1.0;
			for (int i = 0; i < c_count; ++i) {
				float cd = dist(x, y, circles[i].x, circles[i].y)
					/ circles[i].r;
				if (cd < d)
					d = cd;
			}
			float c = 1.0 - d;
			set_image_pixel(img, x, y, (struct color_t){c, c, c});
		}
	}

	write_image_to_bmp(img, "out.bmp");
	destroy_image(img);
}
