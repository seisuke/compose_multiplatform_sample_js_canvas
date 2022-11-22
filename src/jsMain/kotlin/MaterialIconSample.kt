import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.runtime.Composable

@Composable
fun MaterialIcon() {
    TopAppBar {
        Button(
            onClick = {}
        ) {
            Icon(Icons.Filled.Favorite, "favorite")
        }

        Button(
            onClick = {}
        ) {
            Icon(Icons.Filled.Add, "add")
        }
    }
}
