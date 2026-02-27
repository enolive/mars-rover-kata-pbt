import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.checkAll

class DemoTest : DescribeSpec({
  describe("Demo") {
    it("should work") {
      1 + 1 shouldBe 2
    }

    it("should work with PBT") {
      checkAll(Arb.int(), Arb.int()) { x, y ->
        x + y shouldBe x * 2
      }
    }
  }
})