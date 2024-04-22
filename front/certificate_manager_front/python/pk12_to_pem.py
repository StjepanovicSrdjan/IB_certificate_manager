from OpenSSL import crypto

with open("../local-ssl.p12", "rb") as file:
    p12 = crypto.load_pkcs12(file.read(), "031628")

f = open("local-https.key", "w")
f.write(str(crypto.dump_privatekey(crypto.FILETYPE_PEM, p12.get_privatekey())))
f.close()

f = open("local-https.crt", "w")
f.write(str(crypto.dump_certificate(crypto.FILETYPE_PEM, p12.get_certificate())))
f.close()