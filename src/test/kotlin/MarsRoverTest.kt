import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe

class MarsRoverTest : DescribeSpec({
  describe("Mars Rover") {
    describe("Initialization") {
      it("should work") {
        val rover = MarsRover(Location(0, 0), Direction.North)

        rover.location shouldBe Location(0, 0)
        rover.direction shouldBe Direction.North
      }

      it("moves forward facing north") {
        val rover = MarsRover(Location(0, 0), Direction.North)

        val result = rover.forward()

        result.location shouldBe Location(0, 1)
        result.direction shouldBe Direction.North
      }
    }
  }
})

data class MarsRover(val location: Location, val direction: Direction) {
  fun forward(): MarsRover {
    return copy(location = location.copy(y = location.y + 1))
  }
}

data class Location(val x: Int, val y: Int)

enum class Direction {
  North, East, South, West
}