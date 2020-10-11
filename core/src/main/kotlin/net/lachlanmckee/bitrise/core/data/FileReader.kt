package net.lachlanmckee.bitrise.core.data

import java.io.BufferedReader

interface FileReader {
  fun read(name: String): BufferedReader
}
