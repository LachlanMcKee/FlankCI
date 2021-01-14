package net.lachlanmckee.flankci.core.data

import java.io.BufferedReader

interface FileReader {
  fun read(name: String): BufferedReader
}
