import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import io.github.samuolis.pill.PillBarDefaults
import io.github.samuolis.pill.PillNavigationBar
import io.github.samuolis.pill.PillNavigationItem

@Composable
fun Consumer(selected: String, onSelect: (String) -> Unit) {
    PillNavigationBar(
        items = listOf(PillNavigationItem("home", "Home", icon = { BasicText("H") })),
        selectedKey = selected,
        onSelect = onSelect,
        style = PillBarDefaults.glass(),
    )
}
