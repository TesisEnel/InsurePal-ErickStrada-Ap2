package edu.ucne.InsurePal.presentation.utils

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation

object CreditCardFilter : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val trimmed = text.text.take(16)

        var out = ""
        for (i in trimmed.indices) {
            out += trimmed[i]
            if (i % 4 == 3 && i < 15) out += " "
        }

        return TransformedText(AnnotatedString(out), CreditCardOffsetTranslator)
    }
}

private object CreditCardOffsetTranslator : OffsetMapping {
    override fun originalToTransformed(offset: Int): Int {
        if (offset <= 0) return 0
        if (offset >= 16) return 19
        return offset + (offset / 4)
    }

    override fun transformedToOriginal(offset: Int): Int {
        if (offset <= 0) return 0
        if (offset >= 19) return 16
        return offset - (offset / 5)
    }
}