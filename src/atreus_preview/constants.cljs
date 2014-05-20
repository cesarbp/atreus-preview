(ns atreus-preview.constants)

;; Taken from https://github.com/technomancy/atreus-firmware/blob/master/qwerty.json
(def json-example "[[[\"Q\", \"W\", \"E\", \"R\", \"T\", \"Y\", \"U\", \"I\", \"O\", \"P\"],
  [\"A\", \"S\", \"D\", \"F\", \"G\", \"H\", \"J\", \"K\", \"L\", \"SEMICOLON\"],
  [\"Z\", \"X\", \"C\", \"V\", \"B\", \"ALT\", \"N\", \"M\", \"COMMA\", \"PERIOD\", \"SLASH\"],
  [\"ESC\", \"TAB\", \"GUI\", \"SHIFT\", \"BACKSPACE\", \"CTRL\",
   \"SPACE\", \"FN\", \"MINUS\", \"QUOTE\", \"ENTER\"]],
 [[[\"shift\", \"1\"], [\"shift\", \"2\"], [\"shift\", \"LEFT_BRACE\"], [\"shift\", \"RIGHT_BRACE\"],
   [\"shift\", \"BACKSLASH\"], \"PAGE_UP\", \"7\", \"8\", \"9\", [\"shift\", \"8\"]],
  [[\"shift\", \"3\"], [\"shift\", \"4\"], [\"shift\", \"9\"], [\"shift\", \"0\"],
  \"TILDE\", \"PAGE_DOWN\", \"4\", \"5\", \"6\", [\"shift\", \"EQUAL\"]],
  [[\"shift\", \"5\"], [\"shift\", \"6\"], \"LEFT_BRACE\", \"RIGHT_BRACE\", [\"shift\", \"TILDE\"],
   \"ALT\", [\"shift\", \"7\"], \"1\", \"2\", \"3\", \"BACKSLASH\"],
  [[\"function\", 2], [\"shift\", \"INSERT\"], \"GUI\", \"SHIFT\", \"BACKSPACE\", \"CTRL\",
   \"SPACE\", \"FN\", \"PERIOD\", \"0\", \"EQUAL\"]],
 [[\"HOME\", \"UP\", \"END\", \"INSERT\", \"PAGE_UP\", \"UP\", \"F7\", \"F8\", \"F9\", \"F10\"],
  [\"LEFT\", \"DOWN\", \"RIGHT\", \"DELETE\", \"PAGE_DOWN\", \"DOWN\", \"F4\", \"F5\", \"F6\", \"F11\"],
  [\"\", \"\", \"\", \"\", \"\", \"ALT\", \"\", \"F1\", \"F2\", \"F3\", \"F12\"],
  [[\"layer\", 0], \"\", \"GUI\", \"SHIFT\", \"BACKSPACE\", \"CTRL\",
   \"SPACE\", \"FN\", \"\", [\"reset\"]]]]")

(def json-example
  "[[[\"A\"], [\"B\"], [\"C\"]], [[\"A\"], [\"B\"], [\"C\"]]]")

(def layout-example
  (js->clj (js/JSON.parse json-example)))

(def modifiers
  {"CTRL" 0x01
   "SHIFT" 0x02
   "ALT" 0x04
   "GUI" 0x08
   "LEFT_CTRL" 0x01
   "LEFT_SHIFT" 0x02
   "LEFT_ALT" 0x04
   "LEFT_GUI" 0x08
   "RIGHT_CTRL" 0x10
   "RIGHT_SHIFT" 0x20
   "RIGHT_ALT" 0x40
   "RIGHT_GUI" 0x80})

(defn shift
  [n]
  (+ 512 n))

(def key->code
  {"A" 4
   "B" 5
   "C" 6
   "D" 7
   "E" 8
   "F" 9
   "G" 10
   "H" 11
   "I" 12
   "J" 13
   "K" 14
   "L" 15
   "M" 16
   "N" 17
   "O" 18
   "P" 19
   "Q" 20
   "R" 21
   "S" 22
   "T" 23
   "U" 24
   "V" 25
   "W" 26
   "X" 27
   "Y" 28
   "Z" 29
   "1" 30
   "!" (shift 30)
   "2" 31
   "@" (shift 31)
   "3" 32
   "#" (shift 32)
   "4" 33
   "$" (shift 33)
   "5" 34
   "%" (shift 34)
   "6" 35
   "^" (shift 35)
   "7" 36
   "&" (shift 36)
   "8" 37
   "*" (shift 37)
   "9" 38
   "(" (shift 38)
   "0" 39
   ")" (shift 39)
   "ENTER" 40
   "ESC" 41
   "BACKSPACE" 42
   "TAB" 43
   "SPACE" 44
   "MINUS" 45
   "_" (shift 45)
   "EQUAL" 46
   "+" (shift 46)
   "LEFT_BRACE" 47
   "{" (shift 47)
   "RIGHT_BRACE" 48
   "}" (shift 48)
   "BACKSLASH" 49
   "|" (shift 49)
   "NUMBER" 50
   "SEMICOLON" 51
   ":" (shift 51)
   "QUOTE" 52
   "\"" (shift 52)
   "TILDE" 53
   "`" (shift 53)
   "COMMA" 54
   "<" (shift 54)
   "PERIOD" 55
   ">" (shift 55)
   "SLASH" 56
   "?" (shift 56)
   "CAPS_LOCK" 57
   "F1" 58
   "F2" 59
   "F3" 60
   "F4" 61
   "F5" 62
   "F6" 63
   "F7" 64
   "F8" 65
   "F9" 66
   "F10" 67
   "F11" 68
   "F12" 69
   "PRINTSCREEN" 70
   "SCROLL_LOCK" 71
   "PAUSE" 72
   "INSERT" 73
   "HOME" 74
   "PAGE_UP" 75
   "DELETE" 76
   "END" 77
   "PAGE_DOWN" 78
   "RIGHT" 79
   "LEFT" 80
   "DOWN" 81
   "UP" 82
   "NUM_LOCK" 83
   "KEYPAD_SLASH" 84
   "KEYPAD_ASTERIX" 85
   "KEYPAD_MINUS" 86
   "KEYPAD_PLUS" 87
   "KEYPAD_ENTER" 88
   "KEYPAD_1" 89
   "KEND" (shift 89)
   "KEYPAD_2" 90
   "K↓" (shift 90)
   "KEYPAD_3" 91
   "KPGDN" (shift 91)
   "KEYPAD_4" 92
   "K←" (shift 92)
   "KEYPAD_5" 93
   "KEYPAD_6" 94
   "K→" (shift 94)
   "KEYPAD_7" 95
   "KHOME" (shift 95)
   "KEYPAD_8" 96
   "K↑" (shift 96)
   "KEYPAD_9" 97
   "KPGUP" (shift 97)
   "KEYPAD_0" 98
   "KINS" (shift 98)
   "KEYPAD_PERIOD" 99
   "KDEL" (shift 99)
   })

(def code->key (zipmap (vals key->code) (keys key->code)))

(def abbreviations
  {"ENTER" "↵"
   "BACKSPACE" "BKSP"
   "SPACE" "SPC"
   "MINUS" "-"
   "EQUAL" "="
   "LEFT_BRACE" "["
   "RIGHT_BRACE" "]"
   "BACKSLASH" "\\"
   "NUMBER" "NO."
   "SEMICOLON" ";"
   "QUOTE" "'"
   "TILDE" "~"
   "COMMA" ","
   "PERIOD" "."
   "SLASH" "/"
   "CAPS_LOCK" "CAPS"
   "PRINTSCREEN" "PRSC"
   "SCROLL_LOCK" "SCLK"
   "INSERT" "INS"
   "PAGE_UP" "PGUP"
   "DELETE" "DEL"
   "PAGE_DOWN" "PGDN"
   "RIGHT" "→"
   "LEFT" "←"
   "DOWN" "↓"
   "UP" "↑"
   "NUM_LOCK" "NMLK"
   "KEYPAD_SLASH" "K/"
   "KEYPAD_ASTERIX" "K*"
   "KEYPAD_MINUS" "K-"
   "KEYPAD_ENTER" "K↵"
   "KEYPAD_1" "K1"
   "KEYPAD_2" "K2"
   "KEYPAD_3" "K3"
   "KEYPAD_4" "K4"
   "KEYPAD_5" "K5"
   "KEYPAD_6" "K6"
   "KEYPAD_7" "K7"
   "KEYPAD_8" "K8"
   "KEYPAD_9" "K9"
   "KEYPAD_0" "K0"
   "KEYPAD_PERIOD" "K."
   })

(def expansions
  (zipmap (vals abbreviations) (keys abbreviations)))
