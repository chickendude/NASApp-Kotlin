package ch.ralena.nasapp.api

import ch.ralena.nasapp.models.NasaLocationResults
import ch.ralena.nasapp.models.NasaManifestResults
import ch.ralena.nasapp.models.NasaResults
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

val BASE_URL = "https://api.nasa.gov/mars-photos/api/v1/"
val API_KEY: String = "dHlgbskiOH8Antur54g85kyUdxqG2Bz0ks4ouBA8"

var nasaApi: NasaApi = Retrofit.Builder()
		.baseUrl(BASE_URL)
		.addConverterFactory(MoshiConverterFactory.create())
		.build()
		.create(NasaApi::class.java)

var nasaManifestApi: NasaApi = Retrofit.Builder()
		.baseUrl(BASE_URL)
		.addConverterFactory(MoshiConverterFactory.create())
		.build()
		.create(NasaApi::class.java)

var nasaLocationApi: NasaApi = Retrofit.Builder()
		.addConverterFactory(MoshiConverterFactory.create())
		.build()
		.create(NasaApi::class.java)

interface NasaApi {
	@GET("rovers/{rover}/photos")
	fun getPhotosBySol(@Path("rover") rover: String,
					   @Query("sol") sol: Int,
					   @Query("camera") camera: String?,
					   @Query("page") page: Int = 1,
					   @Query("api_key") api_key: String = API_KEY): Call<NasaResults>

	@GET("rovers/{rover}/photos")
	fun getPhotosByEarthDate(@Path("rover") rover: String,
							 @Query("earth_date") earthDate: String,
							 @Query("camera") camera: String?,
							 @Query("page") page: Int = 1,
							 @Query("api_key") api_key: String = API_KEY): Call<NasaResults>

	@GET("manifests/{rover}/")
	fun getRoverManifest(@Path("rover") rover: String,
						 @Query("api_key") api_key: String = API_KEY): Call<NasaManifestResults>

	@GET("https://api.nasa.gov/planetary/earth/imagery/")
	fun getLocationImage(@Query("lat") latitude: Float,
						 @Query("lon") longitude: Float,
						 @Query("api_key") api_key: String = API_KEY): Call<NasaLocationResults>

}