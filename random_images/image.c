#include <stdio.h>
#include <stdlib.h>
#include <time.h>
#include <math.h>
#include "image.h"

/**
 * @brief A function that reads a uint32_t from a file stream
 *
 * \param[in]  file  The input file stream
 * \param[out] value Data pointer where the read value should be stored
 *
 * \return 0 if the operation was successful, -1 if not
 */
static int read_uint32(FILE *file, uint32_t *out);


/**
 * @brief Initialize an image
 *
 * \param[in]  w The width of the image
 * \param[in]  h The height of the image
 *
 * \return The initialized image structure
 */
struct image_t init_image(uint32_t w, uint32_t h)
{
	return (struct image_t) {w, h,
		(struct color_t *)malloc(sizeof(struct color_t) * w * h)};
}

/**
 * @brief Free up the memory used by an image
 *
 * \param[in] image The image to be destroyed
 */
void destroy_image(struct image_t image)
{
	free(image.data);
}

/**
 * @brief A function that reads a uint32_t from a file stream
 *
 * \param[in]  file  The input file stream
 * \param[out] value Data pointer where the read value should be stored
 *
 * \return 0 if the operation was successful, -1 if not
 */
static int read_uint32(FILE *file, uint32_t *out)
{
	unsigned char v[4];
	int bytes_read = fread(v, 1, 4, file);
	if (bytes_read != 4)
		return -1;
	*out = (v[0] << 0U) + (v[1] << 8U) + (v[2] << 16U) + (v[3] << 24U);
	return 0;
}

/**
 * @brief Read an image from a 24bit BMP image file 
 *
 * \param[in] path The path to the image
 *
 * \return An image structure
 */
struct image_t read_bmp_image(const char *path, int *error)
{
	struct image_t image = {0, 0, 0};
	uint32_t w, h;

	// Open the image file
	FILE *file = fopen(path, "r");
	if (!file) {
		*error = 1;
		return image;
	}

	// Get the width and height of the image
	// And go to the starting address of the pixel data
	uint32_t header_size = 0;
	if (
			fseek(file, 0x0E, SEEK_SET) < 0
			|| read_uint32(file, &header_size) < 0
			|| fseek(file, 0x12, SEEK_SET) < 0
			|| read_uint32(file, &w) < 0
			|| read_uint32(file, &h) < 0
			|| fseek(file, 0x0E + header_size, SEEK_SET)) {
		*error = 1;
		fclose(file);
		return image;
	}

	image = init_image(w, h);

	// Buffer used to store color data for a single line
	unsigned char *buff = (unsigned char *)malloc(w * 3);

	// Loop over each line
	for (uint32_t y = 0; y < h; ++y) {
		// Read the next image line
		if (fread(buff, 3, w, file) != w
				|| (w % 4 && fseek(file, w % 4, SEEK_CUR))) {
			destroy_image(image);
			free(buff);
			fclose(file);
			*error = 1;
			return image;
		}
		for (uint32_t x = 0; x < w; ++x) {
			// Bitmaps store lines from the bottom to the top
			// We'll store them from the top to the bottom
			int l = x + (h - y - 1) * w;
			image.data[l].b = buff[3 * x + 0] / 255.0;
			image.data[l].g = buff[3 * x + 1] / 255.0;
			image.data[l].r = buff[3 * x + 2] / 255.0;
		}
	}

	free(buff);
	fclose(file);
	*error = 0;
	return image;
}

/**
 * @brief Write an image to a 24bit BMP file
 *
 * \param[in] image The image to be saved
 * \param[in] path  Path to the output file
 *
 * \return 0 if the operation was successful, -1 if not
 */
int write_image_to_bmp(struct image_t image, const char *path)
{
	uint32_t w = image.w;
	uint32_t h = image.h;

	// Open the output image file
	FILE *file = fopen(path, "w");
	if (!file)
		return -1;

	// Warning: Mostly hardcoded 24Bit BMP header incoming
	unsigned char header[0x7A];
	for (int i = 0; i < 0x7A; ++i)
		header[i] = 0;

	header[0x00] = 0x42;
	header[0x01] = 0x4D;
	header[0x02] = 0xaa;

	header[0x0A] = 0x7A;
	header[0x0E] = 0x6C;
	header[0x1A] = 0x01;
	header[0x1C] = 0x18;
	header[0x22] = 0x30;
	header[0x26] = 0x23;
	header[0x27] = 0x2E;

	header[0x36] = 'B';
	header[0x37] = 'G';
	header[0x38] = 'R';
	header[0x39] = 's';

	header[0x12] = (w >>  0U) & 0xFF;
	header[0x13] = (w >>  8U) & 0xFF;
	header[0x14] = (w >> 16U) & 0xFF;
	header[0x15] = (w >> 24U) & 0xFF;

	header[0x16] = (h >>  0U) & 0xFF;
	header[0x17] = (h >>  8U) & 0xFF;
	header[0x18] = (h >> 16U) & 0xFF;
	header[0x19] = (h >> 24U) & 0xFF;
	// It could've been worse

	// Write the header to the file
	fwrite(header, 0x7A, 1, file);

	// buffer used to sore color data for a single line
	unsigned char *buff = (unsigned char *)malloc(w * 3 + w % 4);

	// Loop over each line
	for (uint32_t y = 0; y < h; ++y) {
		// Write color data to the buffer
		for (uint32_t x = 0; x < w; ++x) {
			int i = x + (h - y - 1) * w;
			buff[x * 3 + 0] = (unsigned char)(image.data[i].b * 255.0);
			buff[x * 3 + 1] = (unsigned char)(image.data[i].g * 255.0);
			buff[x * 3 + 2] = (unsigned char)(image.data[i].r * 255.0);
		}
		// Stupid padding
		for (uint32_t i = 0; i < w % 4; ++i)
			buff[w * 3 + i] = 0;
		// Write the line to the file
		fwrite(buff, w * 3 + w % 4, 1, file);
	}

	fclose(file);
	return 0;
}

/**
 * @brief Scale down an image
 *
 * \param[in] image  The image to be scaled
 * \param[in] factor The scale down factor
 *
 * \return The scaled-down image
 */
struct image_t scale_down_image(struct image_t image, int factor)
{
	int k = factor; // Make my life easier

	// Init the resulting image with a scaled-down size
	struct image_t out = init_image(image.w / k, image.h / k);

	// For benchmarking
	struct timespec start_time, end_time;
	clock_gettime(CLOCK_MONOTONIC, &start_time);

	// Suspiciously easy scaling with no critical sections
	// Loop over each line of the scaled-down image
//#pragma omp parallel for
	for (uint32_t y = 0; y < out.h; ++y) {
		// Now over each pixel
		for (uint32_t x = 0; x < out.w; ++x) {

			// Calculate the average color of k*k pixels
			// By the formula
			// c = sqrt( (c1^2 + c2^2 + ... + cn^2) / n);
			double c[3] = {0, 0, 0};
			for (int i = 0; i < k; ++i) {
				for (int j = 0; j < k; ++j) {
					struct color_t col = *get_image_pixel(image,
							x * k + i, y * k + j);
					c[0] += col.r * col.r;
					c[1] += col.g * col.g;
					c[2] += col.b * col.b;
				}
			}
			for (int i = 0; i < 3; ++i)
				c[i] = sqrt(c[i] / (k * k));

			// Write the pixel to the scaled-down image
			get_image_pixel(out, x, y)->r = c[0];
			get_image_pixel(out, x, y)->g = c[1];
			get_image_pixel(out, x, y)->b = c[2];
		}
	}
	
	// Get the time that was required to do the scaling
	clock_gettime(CLOCK_MONOTONIC, &end_time);
	long ns = end_time.tv_nsec - start_time.tv_nsec;
	double sec = end_time.tv_sec - start_time.tv_sec;
	sec += ns / 1000000000.0;
	printf("Resize took %f seconds\n", sec);

	return out;
}

