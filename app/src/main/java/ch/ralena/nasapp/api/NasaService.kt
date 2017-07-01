package ch.ralena.nasapp.api

import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

var nasaApi: NasaApi = Retrofit.Builder()
		.baseUrl(BASE_URL)
		.addConverterFactory(MoshiConverterFactory.create())
		.build()
		.create(NasaApi::class.java)

