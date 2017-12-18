[![Release](https://jitpack.io/v/net.skycanvas/fluks.svg)](https://jitpack.io/#net.skycanvas/fluks)

# fluks
Fluent native type-safe Kotlin interface to SQLite/SQLCipher

## Usage
```kotlin

val users = Table("users")
val id = Column<Int>("id")
val name = Column<String?>("name")
val email = Column<String>("email")

val db = Connection(SQLiteDriver("/path/to/database.sqlite"))

if (!db.scalar<Boolean>(users.exists())) {
    db.exec(
        users.create {
            it.column(column = id, autoincrement = true, primaryKey = true)
            it.column(name)
            it.column(column = email, unique = true)
        }
    )
}

db.exec(
    users.insert {
        it[name] = "John Smith"
        it[email] = "john.smith@example.com"
    }
)


for (user in db.query(users.select())) {
    println("ID:${user[id]}, Name:${user[name]?:"no name"}, Email: ${user[email]}")"
}

```


## Installation

```groovy
repositories {
    maven { url 'https://jitpack.io' }
}

dependencies {
    // for SQLite
    compile 'net.skycanvas.flux:flux-core:0.1.7'
    // for SQLite + SQLCipher
    compile 'net.skycanvas.flux:flux-cipher:0.1.7'

}
```
