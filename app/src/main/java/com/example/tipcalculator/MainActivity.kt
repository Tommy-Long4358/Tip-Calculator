package com.example.tipcalculator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.annotation.VisibleForTesting
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tipcalculator.ui.theme.TipCalculatorTheme
import java.text.NumberFormat

class MainActivity : ComponentActivity()
{
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContent {
            TipCalculatorTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    TipTimeLayout()
                }
            }
        }
    }
}

@Composable
fun TipTimeLayout() {
    // Keep track of user inputs
    var amountInput: String by remember { mutableStateOf("") }
    var tipInput: String by remember { mutableStateOf("") }
    var roundUp: Boolean by remember { mutableStateOf(false) }

    // Convert inputs to Double
    val amount: Double = amountInput.toDoubleOrNull() ?: 0.0
    val tipPercent: Double = tipInput.toDoubleOrNull() ?: 0.0

    // Calculate tip and total
    val tipAmount: Double = calculateTip(amount, tipPercent, roundUp)
    val totalAmount: Double = calculateTotal(amount, tipAmount)

    // Format tip and total
    val formattedTipAmount: String = NumberFormat.getCurrencyInstance().format(tipAmount)
    val formattedTotalAmount: String = NumberFormat.getCurrencyInstance().format(totalAmount)

    Column(
        modifier = Modifier
            .statusBarsPadding() // adds padding to top to account for height of status bar
            .padding(40.dp) // adds padding left and right of screen
            .verticalScroll(rememberScrollState()) // enable vertical scroll and remember scroll state
            .safeDrawingPadding(), // adds padding to ensure that UI isn't covered by system UI element
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = stringResource(R.string.calculate_tip),
            modifier = Modifier
                .padding(bottom = 16.dp, top = 40.dp)
                .align(alignment = Alignment.Start)
        )

        EditNumberField(
            label = R.string.bill_amount_text,
            leadingIcon = R.drawable.money,
            keyboardOptions = KeyboardOptions( // Set action button for both input text boxes
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Next // Makes an arrow on keyboard prompting user to go input the next text box, tip
            ),
            value = amountInput,
            onValueChange = { amountInput = it }, // use hoisted state
            modifier = Modifier
                .padding(bottom = 32.dp)
                .fillMaxWidth()
        )

        EditNumberField(
            label = R.string.tip_percentage_text,
            leadingIcon = R.drawable.percent,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done // Shows a checkmark on keyboard which allows user to stop entering input into text boxes
            ),
            value = tipInput,
            onValueChange = { tipInput = it },
            modifier = Modifier
                .padding(bottom = 32.dp)
                .fillMaxWidth()
        )

        RoundTipRow(
            roundUp = roundUp,
            onRoundUpChanged = { roundUp = it },
            modifier = Modifier
                .padding(bottom = 32.dp)
        )

        Text(
            text = stringResource(R.string.tip_amount, formattedTipAmount),
            style = MaterialTheme.typography.displaySmall,
            fontSize = 30.sp,
        )

        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = stringResource(id = R.string.total_amount, formattedTotalAmount),
            style = MaterialTheme.typography.displaySmall,
            fontSize = 30.sp
        )
    }
}

@Composable
fun EditNumberField(
    @StringRes label: Int,
    @DrawableRes leadingIcon: Int,
    keyboardOptions: KeyboardOptions,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {

    TextField(
        modifier = modifier,
        value = value,
        leadingIcon = { Icon(painter = painterResource(id = leadingIcon), contentDescription = null) },
        onValueChange = onValueChange,
        label = { Text(text = stringResource(label)) }, // Assign label to be a text composable saying "Bill Amount"
        singleLine = true, // Condenses box to a single, horizontally scrollable line from multiple lines
        keyboardOptions = keyboardOptions // Configure keyboard displayed to enter only digits
    )
}

@Composable
fun RoundTipRow(
    roundUp: Boolean,
    onRoundUpChanged: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .size(52.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(id = R.string.round_up_tip),
            modifier = Modifier
                .padding(bottom = 32.dp)
        )

        Switch(
            modifier = modifier
                .fillMaxWidth()
                .wrapContentWidth(Alignment.End), // Align Switch composable to end of the screen
            checked = roundUp,
            onCheckedChange = onRoundUpChanged
        )
    }
}

/**
 * Calculates the tip based on the user input and format the tip amount
 * according to the local currency.
 * Example would be "$10.00".
 */
@VisibleForTesting // Makes method public, but only for testing purposes
internal fun calculateTip(
    amount: Double,
    tipAmount: Double,
    roundUp: Boolean
): Double {
    var tip: Double = tipAmount / 100 * amount

    if (roundUp) {
        // Rounds up an integer
        tip = kotlin.math.ceil(tip)
    }

    return tip
}

private fun calculateTotal(amount: Double, tipAmount: Double): Double {
    return amount + tipAmount
}

@Preview(showBackground = true)
@Composable
fun TipTimeLayoutPreview() {
    TipCalculatorTheme {
        TipTimeLayout()
    }
}