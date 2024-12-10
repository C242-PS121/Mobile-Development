import com.google.gson.annotations.SerializedName

data class DetailProductResponse(
    @SerializedName("data") val data: ProductDetail
)

data class ProductDetail(
    @SerializedName("id") val id: String,
    @SerializedName("owner_id") val ownerId: String,
    @SerializedName("main_img_url") val mainImageUrl: String,
    @SerializedName("image_urls") val imageUrls: List<String>,
    @SerializedName("name") val name: String,
    @SerializedName("price") val price: Int,
    @SerializedName("description") val description: String,
    @SerializedName("clothing_type") val clothingType: String,
    @SerializedName("sold") val sold: Boolean
)
