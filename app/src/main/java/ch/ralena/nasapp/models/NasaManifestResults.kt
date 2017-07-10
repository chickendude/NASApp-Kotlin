package ch.ralena.nasapp.models

class NasaManifestResults(
		val photo_manifest: Manifest
)

class Manifest(
	val name: String,
	val photos: List<ManifestPhoto>
)

class ManifestPhoto(
		val cameras: List<String>,
		val sol: String
)