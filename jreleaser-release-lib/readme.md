### Generate GPG key

To generate a GPG key, you can use the following command in your terminal:

```bash
gpg --full-generate-key
```

Follow the prompts to set up your key. You can choose the default options for most of them, but make sure to remember the passphrase you
set, as you'll need it later.

Then, you can list your GPG keys to find the key ID:

```bash
gpg --list-secret-keys --keyid-format LONG
```

Then export your public key:

```bash
gpg --export YOUR_KEY_ID > public.gpg
```

To export your private key, you can use the following command:

```bash
gpg --export-secret-key YOUR_KEY_ID > private.gpg
```

To encode a public key in base64 format, you can use the following command in your terminal:

```bash
gpg --armor --export <KEY_ID> | base64 -w 0 > encoded_public.txt
```

To encode a private key in base64 format, you can use the following command in your terminal:

```bash
base64 -w 0 path/to/private-key.gpg > encoded_key.txt
```

And use those as base64 encoded strings in your GitHub Actions workflow via the `JRELEASER_GPG_PUBLIC_KEY` and `JRELEASER_GPG_SECRET_KEY`
secrets, and the passphrase as `JRELEASER_GPG_PASSPHRASE`.


