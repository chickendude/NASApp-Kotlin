package ch.ralena.nasapp

import ch.ralena.nasapp.api.nasaApi
import ch.ralena.nasapp.fragments.roverNames
import org.junit.Assert.assertEquals
import org.junit.Test

class RoverApiTest {
	@Test
	fun solApiConvertedToObject() {
		// arrange
		val roverName1 = roverNames[0]
		val result = nasaApi.getPhotosBySol(roverName1, 234, "fhaz").execute()

		// act
		val nasaResults = result.body()
		val roverName2 = nasaResults.photos[0].rover.name

		// assert
		assertEquals(roverName1, roverName2)
	}

	@Test
	fun earthApiConvertedToObject() {
		// arrange
		val roverName1 = roverNames[0]
		val result = nasaApi.getPhotosByEarthDate(roverName1, "2015-6-3", "fhaz").execute()

		// act
		val nasaResults = result.body()
		val roverName2 = nasaResults.photos[0].rover.name

		// assert
		assertEquals(roverName1, roverName2)
	}

	@Test
	fun roverManifestGetsListOfPictures() {
		// arrange
		val result = nasaApi.getRoverManifest(roverNames[0]).execute()

		// act

		// assert
		assert(result.body().photo_manifest.photos.isNotEmpty())
	}

	@Test
	fun nasaImageFromlatitudeLongitude() {
		// arrange
		val lat = 30f
		val long = 30f
		val date = "2014-10-10"

		// act
		val result = nasaApi.getLocationImage(lat, long, date).execute()
		val imageUrl = result.body().url

		// assert
		assert(imageUrl.contains("http"))
	}

	@Test
	fun locationAssetsHasResults() {
		// arrange
		val lat = 30f
		val long = 30f
		val date = "2014-10-10"

		// act
		val result = nasaApi.getLocationAssets(lat, long, date).execute()

		// assert
		assert(result.body().results.isNotEmpty())
	}

}