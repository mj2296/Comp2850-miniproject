package org.jetbrains.amper.ktor

import org.junit.jupiter.api.io.TempDir
import java.io.File
import java.nio.file.Path
import java.time.LocalDate
import java.time.LocalTime
import kotlin.test.*

class TimetableStoreTest {

    @TempDir
    lateinit var tempDir: Path

    private fun store(): TimetableStore {
        val copy = tempDir.resolve("sessions.csv").toFile()
        File("data/sessions.csv").copyTo(copy, overwrite = true)
        return TimetableStore(copy.path)
    }

    @Test
    fun `all returns every event`() {
        val s = store()

        val result = s.all()

        assertEquals(10, result.size)
    }

    @Test
    fun `first event parses correctly`() {
        val e = store().all()[0]

        assertEquals(1, e.id)
        assertEquals(LocalDate.of(2026, 11, 23), e.date)
        assertEquals(LocalTime.of(10, 0), e.start_time)
        assertNull(e.previous_location)
    }

    @Test
    fun `existing previous location parses as text successfully`() {
        val e = store().find(3)

        assertEquals("Mechanical Engineering LT A (1.33)", e?.previous_location)
    }

    @Test
    fun `find returns an existing event`() {
        val e = store().find(3)

        assertNotNull(e)
        assertEquals("Andrew Treanor", e.staff_member)
    }

    @Test
    fun `find returns null for an unknown id`() {
        val e = store().find(999)

        assertNull(e)
    }

    @Test
    fun `changeLocation updates and saves the event successfully`() {
        val s = store()

        val ok = s.changeLocation(1, "Bragg 2.05")

        assertTrue(ok)

        val path = tempDir.resolve("sessions.csv").toString()
        val updated = TimetableStore(path).find(1)

        assertNotNull(updated)
        assertEquals("Bragg 2.05", updated.location)
        assertEquals("Michael Sadler RBLT (LG.X04)", updated.previous_location)
    }

    @Test
    fun `changeLocation returns false for an unknown id`() {
        val s = store()

        val result = s.changeLocation(999, "X")

        assertFalse(result)
        assertEquals(10, s.all().size)
    }

    @Test
    fun `changeLocation rejects locations that containi commas`() {
        val s = store()

        val result = s.changeLocation(1, "Bragg, 2.05")

        assertFalse(result)
        assertEquals("Michael Sadler RBLT (LG.X04)", s.find(1)?.location)
    }
}
