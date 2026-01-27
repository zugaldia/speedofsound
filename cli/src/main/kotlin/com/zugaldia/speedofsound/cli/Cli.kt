package com.zugaldia.speedofsound.cli

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.main
import com.github.ajalt.clikt.core.subcommands
import com.zugaldia.speedofsound.core.APPLICATION_NAME

class Cli : CliktCommand(name = APPLICATION_NAME) {
    override val printHelpOnEmptyArgs = true
    override fun run() = Unit
}

fun main(args: Array<String>) = Cli()
    .subcommands(RecordCommand())
    .main(args)
