package com.example.cloaktalk.data.local.database

                import androidx.room.RoomDatabase
                import androidx.sqlite.db.SupportSQLiteDatabase

                /**
                 * Database callback for pre-populating the CloakTalk database with initial data.
                 *
                 * This callback is triggered when the database is first created (onCreate) and
                 * inserts the three base encryption algorithms that the app supports:
                 * - Caesar Cipher
                 * - Vigenère Cipher
                 * - Substitution Cipher
                 *
                 * Uses raw SQL to avoid deadlock from calling getInstance() during database construction.
                 *
                 * @see RoomDatabase.Callback
                 */
                class DatabaseCallback : RoomDatabase.Callback() {

                    /**
                     * Called when the database is created for the first time.
                     *
                     * Inserts the predefined base algorithms using raw SQL to avoid
                     * the deadlock that would occur from calling getInstance().
                     *
                     * @param db The SQLite database instance
                     */
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)

                        // Insert base algorithms using raw SQL (avoids getInstance() deadlock)
                        db.execSQL(
                            """
                            INSERT OR IGNORE INTO base_algorithm (baseAlgoName, description) VALUES
                            ('Caesar Cipher', 'A substitution cipher that shifts letters by a fixed number of positions. One of the oldest and simplest encryption techniques, named after Julius Caesar.'),
                            ('Vigenère Cipher', 'A polyalphabetic substitution cipher using a keyword to determine shifts. More secure than Caesar cipher as it uses multiple shift values.'),
                            ('Substitution Cipher', 'A digraph substitution cipher using a 5x5 key matrix. Encrypts pairs of letters, making frequency analysis more difficult.')
                            """.trimIndent()
                        )
                    }
                }