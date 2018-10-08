#include <stdint.h>

/**
 * @brief A color
 *
 * A structure that represents an RGB color with float numbers
 */
struct color_t
{
	float r, g, b;
};

/**
 * @brief An image
 *
 * A structure that represents an image
 */
struct image_t
{
	uint32_t w, h;
	struct color_t *data; /**< Color data for each pixel of the
							image. Should have a length of w * h.
							The pixels are stored left-to-right,
							top-to-bottom. */
};

/**
 * @brief Access the color of an image's pixel
 *
 * \param[in] image The image
 * \param[in] x     The x coordinate of the pixel
 * \param[in] y     The y coordinate of the pixel
 *
 * \return Pointer to the color of the pixel at (x, y)
 */
static inline struct color_t *get_image_pixel(struct image_t image,
		uint32_t x, uint32_t y)
{
	return &image.data[x + y * image.w];
}

/**
 * @brief Set the color of an image's pixel
 *
 * \param[in] image The image
 * \param[in] x     The x coordinate of the pixel
 * \param[in] y     The y coordinate of the pixel
 * \param[in] color The color the pixel should be set to
 *
 * \return Pointer to the color of the pixel at (x, y)
 */
static inline void set_image_pixel(struct image_t image,
		uint32_t x, uint32_t y, struct color_t color)
{
	image.data[x + y * image.w] = color;
}

/**
 * @brief Initialize an image
 *
 * \param[in]  w The width of the image
 * \param[in]  h The height of the image
 *
 * \return The initialized image structure
 */
struct image_t init_image(uint32_t w, uint32_t h);

/**
 * @brief Free up the memory used by an image
 *
 * \param[in] image The image to be destroyed
 */
void destroy_image(struct image_t image);


/**
 * @brief Read an image from a 24bit BMP image file 
 *
 * \param[in] path The path to the image
 *
 * \return An image structure
 */
struct image_t read_bmp_image(const char *path, int *error);

/**
 * @brief Write an image to a 24bit BMP file
 *
 * \param[in] image The image to be saved
 * \param[in] path  Path to the output file
 *
 * \return 0 if the operation was successful, -1 if not
 */
int write_image_to_bmp(struct image_t image, const char *path);

/**
 * @brief Scale down an image
 *
 * \param[in] image  The image to be scaled
 * \param[in] factor The scale down factor
 *
 * \return The scaled-down image
 */
struct image_t scale_down_image(struct image_t image, int factor);

