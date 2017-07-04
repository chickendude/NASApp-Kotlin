package ch.ralena.nasapp

import ch.ralena.nasapp.api.nasaApi
import ch.ralena.nasapp.fragments.cameraNames
import ch.ralena.nasapp.fragments.roverNames
import org.junit.Assert.assertEquals
import org.junit.Test

class RoverApiTest {
	@Test
	fun solApiConvertedToObject() {
		// arrange
		val roverName1 = roverNames[0]
		val result = nasaApi.getPhotosBySol(roverName1, 234, cameraNames.keys.first()).execute()

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
}