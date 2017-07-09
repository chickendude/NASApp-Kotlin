package ch.ralena.nasapp.models

class NasaResults(
		val photos: List<Photo>
)

class Photo(
		val camera: Camera,
		val img_src: String,
		val earth_date: String,
		val rover: Rover
)

class Camera(
		val name: String,
		val full_name: String
)

class Rover(
		val name: String
)