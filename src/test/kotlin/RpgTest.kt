import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.ints.shouldBeGreaterThanOrEqual
import io.kotest.matchers.ints.shouldBeLessThanOrEqual
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.map
import io.kotest.property.arbitrary.orNull
import io.kotest.property.checkAll

class RpgTest : DescribeSpec({
    describe("RPG") {
        describe("initial") {
            it("character should be alive") {
                val character = RpgCharacter()
                character.isAlive shouldBe true
            }
            it("character should have full health") {
                val character = RpgCharacter()
                character.health shouldBe 1000
            }
        }
        describe("combat") {
            it("character deals damage") {
                val hero = RpgCharacter()
                val enemy = RpgCharacter()
                hero.attacks(enemy)
                enemy.health shouldBe 990
            }
            it("character has always positive health") {
                val hero = RpgCharacter()
                val enemy = RpgCharacter()
                val initHealth = enemy.health
                checkAll(Arb.int()) { damage ->
                    hero.attacks(enemy, damage)
                    enemy.health shouldBeGreaterThanOrEqual 0
                    enemy.health shouldBeLessThanOrEqual initHealth
                }
            }

            it("negative damage does not do anything") {
                val hero = RpgCharacter()
                val enemy = RpgCharacter()
                val initHealth = enemy.health
                checkAll(Arb.int(max = 0)) { damage ->
                    hero.attacks(enemy, damage)
                    enemy.health shouldBe initHealth
                }
            }


            it("character cannot damage itself") {
                val hero = RpgCharacter()
                hero.attacks(hero)
                hero.health shouldBe 1000
            }
            it("when damage exceeds current health character dies") {
                val hero = RpgCharacter()
                val enemy = RpgCharacter()
                checkAll(Arb.int(min = enemy.health)) { damage ->
                    hero.attacks(enemy, damage)
                    enemy.isAlive shouldBe false
                }
            }
        }
        describe("Heal") {
            it("character can heal itself") {
                val hero = RpgCharacter(1000)
                val enemy = RpgCharacter()
                enemy.attacks(hero, 10)

                hero.heal(1)

                hero.health shouldBe 991
            }
            it("character can heal to full amount") {
                val hero = RpgCharacter(1000)
                val enemy = RpgCharacter()
                enemy.attacks(hero, 10)

                hero.heal()

                hero.health shouldBe 1000
            }
            it("character cannot overheal") {
                val arbHero = Arb.int(min = 1).map { RpgCharacter(it) }

                checkAll(arbHero, Arb.int(min = 1)) { hero, amount ->
                    val maxHealth = hero.health
                    hero.heal(amount)
                    hero.health shouldBe maxHealth
                }
            }
            it("dead character cannot heal itself") {
                val arbHero = Arb.int(min = 1).map { RpgCharacter(it) }
                val enemy = RpgCharacter()

                checkAll(arbHero, Arb.int(min = 1).orNull()) { hero, amount ->
                    enemy.attacks(hero, hero.health)

                    hero.heal(amount)

                    hero.isAlive shouldBe false
                    hero.health shouldBe 0
                }
            }
        }
    }
})

private const val WEAPON_DAMAGE = 10

class RpgCharacter(var health: Int = 1000) {
    var isAlive = true

    fun attacks(other: RpgCharacter, damage: Int = WEAPON_DAMAGE) {
        if (other == this) return
        if (damage <= 0) return
        other.health -= damage
        if (other.health <= 0) {
            other.health = 0
            other.isAlive = false
        }
    }

    private val maxHealth = health

    fun heal(amount: Int? = null) {
        if (!isAlive) {
            return
        }

        if (amount == null) {
            this.health = maxHealth
            return
        }
        val newHealth: Long = (this.health.toLong() + amount).coerceAtMost(maxHealth.toLong())
        this.health = newHealth.toInt()
    }
}
