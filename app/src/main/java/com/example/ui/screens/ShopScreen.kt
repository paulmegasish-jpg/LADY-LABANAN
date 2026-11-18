package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.data.model.CategoryEntity
import com.example.data.model.ProductEntity
import com.example.i18n.AppStrings
import com.example.ui.components.LuxuryProductCard
import com.example.ui.components.formatInr
import com.example.ui.theme.ChampagneGold
import com.example.ui.theme.DeepRose
import com.example.ui.theme.ObsidianBlack
import com.example.ui.theme.PureWhite
import com.example.ui.viewmodel.CatalogFilterState
import com.example.ui.viewmodel.SortOption

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShopScreen(
    strings: AppStrings,
    categories: List<CategoryEntity>,
    products: List<ProductEntity>,
    filterState: CatalogFilterState,
    wishlistIds: Set<String>,
    onQueryChange: (String) -> Unit,
    onSelectCategory: (String) -> Unit,
    onApplyFilters: (String, Int, SortOption) -> Unit,
    onResetFilters: () -> Unit,
    onOpenProduct: (String) -> Unit,
    onToggleWishlist: (String) -> Unit
) {
    var showFilterSheet by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Surface(
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 2.dp
        ) {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = filterState.query,
                        onValueChange = onQueryChange,
                        placeholder = { Text(strings.searchHint, style = MaterialTheme.typography.bodyMedium) },
                        leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null, tint = DeepRose) },
                        trailingIcon = {
                            if (filterState.query.isNotBlank()) {
                                IconButton(onClick = { onQueryChange("") }) {
                                    Icon(Icons.Filled.Close, contentDescription = "Clear search")
                                }
                            }
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("shop_search_input")
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Surface(
                        onClick = { showFilterSheet = true },
                        shape = RoundedCornerShape(14.dp),
                        color = ObsidianBlack,
                        modifier = Modifier
                            .height(52.dp)
                            .testTag("shop_filter_button")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Filled.FilterList, contentDescription = "Filter", tint = ChampagneGold)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Filter", style = MaterialTheme.typography.labelMedium, color = PureWhite)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Category Chips
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    item {
                        FilterChip(
                            selected = filterState.selectedCategory == "All",
                            onClick = { onSelectCategory("All") },
                            label = { Text("All (${products.size})") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = DeepRose,
                                selectedLabelColor = PureWhite
                            )
                        )
                    }
                    items(categories.filter { it.isActive }, key = { it.id }) { cat ->
                        val label = strings.categoryNames[cat.name] ?: cat.name
                        FilterChip(
                            selected = filterState.selectedCategory == cat.name,
                            onClick = { onSelectCategory(cat.name) },
                            label = { Text(label) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = DeepRose,
                                selectedLabelColor = PureWhite
                            )
                        )
                    }
                }
            }
        }

        if (products.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "No silhouettes match your current filter",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedButton(onClick = onResetFilters) {
                        Text("Reset Filters")
                    }
                }
            }
        } else {
            BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                val columns = when {
                    maxWidth >= 840.dp -> 4
                    maxWidth >= 600.dp -> 3
                    else -> 2
                }
                LazyVerticalGrid(
                    columns = GridCells.Fixed(columns),
                    contentPadding = PaddingValues(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    items(products, key = { it.id }) { product ->
                        LuxuryProductCard(
                            product = product,
                            isWishlisted = wishlistIds.contains(product.id),
                            onProductClick = { onOpenProduct(product.id) },
                            onToggleWishlist = { onToggleWishlist(product.id) }
                        )
                    }
                }
            }
        }
    }

    if (showFilterSheet) {
        var tempSize by remember(filterState) { mutableStateOf(filterState.selectedSize) }
        var tempMaxPrice by remember(filterState) { mutableFloatStateOf(filterState.maxPriceInr.toFloat()) }
        var tempSort by remember(filterState) { mutableStateOf(filterState.sortBy) }

        ModalBottomSheet(onDismissRequest = { showFilterSheet = false }) {
            Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp)) {
                Text("Refine Your Boutique Edit", style = MaterialTheme.typography.headlineMedium)
                Spacer(modifier = Modifier.height(14.dp))

                Text("Sort By", style = MaterialTheme.typography.titleSmall)
                Spacer(modifier = Modifier.height(8.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(SortOption.entries) { opt ->
                        FilterChip(
                            selected = tempSort == opt,
                            onClick = { tempSort = opt },
                            label = { Text(opt.label) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))
                Text("Size", style = MaterialTheme.typography.titleSmall)
                Spacer(modifier = Modifier.height(8.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(listOf("All", "XS", "S", "M", "L", "XL", "XXL")) { sz ->
                        FilterChip(
                            selected = tempSize == sz,
                            onClick = { tempSize = sz },
                            label = { Text(sz) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))
                Text("Max Price: ${formatInr(tempMaxPrice.toInt())}", style = MaterialTheme.typography.titleSmall)
                Slider(
                    value = tempMaxPrice,
                    onValueChange = { tempMaxPrice = it },
                    valueRange = 1000f..20000f,
                    steps = 18
                )

                Spacer(modifier = Modifier.height(14.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            onResetFilters()
                            showFilterSheet = false
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Reset")
                    }
                    Button(
                        onClick = {
                            onApplyFilters(tempSize, tempMaxPrice.toInt(), tempSort)
                            showFilterSheet = false
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Apply")
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}
