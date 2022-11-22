import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

@Composable
fun LazyListView() {
    var list by remember { mutableStateOf( listOf("A", "B", "C", "D"))}
    Column {
        Button(
            onClick = {
                list = list + listOf(
                    "NEW Item"
                )
            }
        ) {

        }

        LazyColumn {
            items(list) {
                Text(text = it)
            }
        }
    }
}
