DESCRIPTION = ".rom image creation tool"
LICENSE = "CLOSED"

SRC_URI = "file://${BPN}.tar.xz"
S = "${WORKDIR}/${BPN}"
PV = "${LTRX_PRODUCT_VERSION}"

# runs on the host
BBCLASSEXTEND =+ "native nativesdk"

inherit autotools-brokensep

do_install() {
	install -D -m 0755 ${B}/bin/ltrx-mkimage ${D}${bindir}/ltrx-mkimage
	install -D -m 0755 ${S}/ltrx_image.h ${D}${includedir}/ltrx_image.h
}

