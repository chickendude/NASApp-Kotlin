package ch.ralena.nasapp.models

class NasaLocationResults(
		val date: String,
		val url: String
)

class NasaLocationAssetResults(
		val results: List<LocationAsset>
)

class LocationAsset(
		val date: String
)