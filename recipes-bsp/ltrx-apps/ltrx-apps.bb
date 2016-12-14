DESCRIPTION = "Lantronix apps SDK installation recipe"
LICENSE = "CLOSED"

SDKFILE = "ltrx-apps-sdk-${PV}-${MACHINE}.tar.xz"

SRC_URI = "file://${SDKFILE};unpack=no"

S = "${WORKDIR}"

PV = "${LTRX_PRODUCT_VERSION}"

RDEPENDS_${PN} = "avahi-daemon"
RDEPENDS_${PN} += "bridge-utils"
RDEPENDS_${PN} += "curl"
RDEPENDS_${PN} += "dhcp-server"
RDEPENDS_${PN} += "dnsmasq"
RDEPENDS_${PN} += "dropbear"
RDEPENDS_${PN} += "ebtables"
RDEPENDS_${PN} += "e2fsprogs"
RDEPENDS_${PN} += "ethtool"
RDEPENDS_${PN} += "fuse"
RDEPENDS_${PN} += "iperf"
RDEPENDS_${PN} += "iproute2"
RDEPENDS_${PN} += "iptables"
RDEPENDS_${PN} += "iw"
RDEPENDS_${PN} += "libbsd-mini-dev"
RDEPENDS_${PN} += "libesmtp"
RDEPENDS_${PN} += "libnfnetlink"
RDEPENDS_${PN} += "libnl"
RDEPENDS_${PN} += "libpcap"
RDEPENDS_${PN} += "ltrx-mkimage"
RDEPENDS_${PN} += "ltrx-wpa-supplicant"
RDEPENDS_${PN} += "miniupnpd"
RDEPENDS_${PN} += "mtd-utils"
RDEPENDS_${PN} += "mtd-utils-ubifs"
RDEPENDS_${PN} += "net-snmp"
RDEPENDS_${PN} += "nginx"
RDEPENDS_${PN} += "openssl"
RDEPENDS_${PN} += "traceroute"
RDEPENDS_${PN} += "tzdata"
RDEPENDS_${PN} += "vsftpd"
RDEPENDS_${PN} += "wireless-tools"
RDEPENDS_${PN} += "zeromq"

INSANE_SKIP_${PN} += "dev-deps dev-so"

FILES_${PN} += "/http ${libdir}"

# leave solibs in main package
FILES_${PN}-dev = "${includedir}"

do_configure[noexec] = "1"
do_compile[noexec] = "1"

do_install() {
	tar xf ${SDKFILE} -C ${D} --no-same-owner
}
