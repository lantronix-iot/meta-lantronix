# ltrx_wpa_supplicant

PV = "${LTRX_PRODUCT_VERSION}_${LTRX_KERNEL_MACHINE}"

SRC_URI = "file://ltrx_wpa_supplicant-${PV}.tar.gz \
           file://wpa_supplicant.conf \
"

S = "${WORKDIR}/ltrx_wpa_supplicant-${PV}"

LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://README;beginline=7;endline=59;md5=600e9eef20976b57cf2a98eea3b8dfb8 \
                    file://wpa_supplicant/wpa_supplicant.c;beginline=1;endline=6;md5=7a7767eafa580c6b697bc41ca8b37953"

inherit systemd

SYSTEMD_SERVICE_${PN} = "wpa_supplicant.service wpa_supplicant-nl80211@.service wpa_supplicant-wired@.service"
SYSTEMD_AUTO_ENABLE = "disable"

WPA_PACKAGES = "wpa-supplicant-ap-support wpa-supplicant-cli \
               wpa-supplicant-dbus-introspection wpa-supplicant-dbus-new \
               wpa-supplicant-dbus-old wpa-supplicant-debug-syslog \
               wpa-supplicant-eap wpa-supplicant-passphrase wpa-supplicant-wps \
"

PROVIDES += "wpa-supplicant"
ALLOW_EMPTY_wpa-supplicant = "1"
PACKAGES_prepend = "wpa-supplicant ${WPA_PACKAGES} "
FILES_wpa-supplicant-passphrase = "${bindir}/wpa_passphrase"
FILES_wpa-supplicant-cli = "${sbindir}/wpa_cli"
FILES_${PN} += "${datadir}/dbus-1/system-services/*"
CONFFILES_${PN} += "${sysconfdir}/wpa_supplicant.conf"

LTRX_WPA_SUPPLICANT_CONFIG_ENABLE = "CONFIG_IEEE80211AC CONFIG_IEEE80211N \
                                     CONFIG_IEEE80211R CONFIG_IEEE80211W \
                                     CONFIG_INTERWORKING \
                                     CONFIG_INTERNAL_LIBTOMMATH \
"
LTRX_WPA_SUPPLICANT_CONFIG_DISABLE = "CONFIG_SMARTCARD"

LTRX_WPA_SUPPLICANT_DBUS_OLD_SERVICE = "fi.epitest.hostap.WPASupplicant"
LTRX_WPA_SUPPLICANT_DBUS_NEW_SERVICE = "fi.w1.wpa_supplicant1"

DEPENDS += "ltrx-apps libnl"

RRECOMMENDS_${PN} = "wpa-supplicant-passphrase wpa-supplicant-cli"

PACKAGECONFIG_append = " ${WPA_PACKAGES} dbus gnutls libnl openssl readline"

PACKAGECONFIG[gnutls] = ",,gnutls,"

PACKAGECONFIG[libnl] = ",,libnl,"
LTRX_WPA_SUPPLICANT_CONFIG_ENABLE += "${@bb.utils.contains('PACKAGECONFIG', 'libnl', 'CONFIG_LIBNL32', '', d)}"
LTRX_WPA_SUPPLICANT_CONFIG_DISABLE += "${@bb.utils.contains('PACKAGECONFIG', 'libnl', '', 'CONFIG_DRIVER_NL80211', d)}"

PACKAGECONFIG[wpa-supplicant-eap] = ",,,"
LTRX_WPA_SUPPLICANT_CONFIG_ENABLE += "${@bb.utils.contains('PACKAGECONFIG', 'wpa-supplicant-eap', 'CONFIG_EAP_', '', d)}"
LTRX_WPA_SUPPLICANT_CONFIG_DISABLE += "${@bb.utils.contains('PACKAGECONFIG', 'wpa-supplicant-eap', '', 'CONFIG_EAP', d)}"

PACKAGECONFIG[wpa-supplicant-ap-support] = ",,,"
LTRX_WPA_SUPPLICANT_CONFIG_ENABLE += "${@bb.utils.contains('PACKAGECONFIG', 'wpa-supplicant-ap-support', 'CONFIG_AP CONFIG_P2P', '', d)}"

PACKAGECONFIG[wpa-supplicant-wps] = ",,,"
LTRX_WPA_SUPPLICANT_CONFIG_ENABLE += "${@bb.utils.contains('PACKAGECONFIG', 'wpa-supplicant-wps', 'CONFIG_WPS', '', d)}"

PACKAGECONFIG[openssl] = ",,openssl,"
LTRX_WPA_SUPPLICANT_TLS = "${@bb.utils.contains('PACKAGECONFIG', 'openssl', 'openssl', 'internal', d)}"
LTRX_WPA_SUPPLICANT_CONFIG_DISABLE += "${@bb.utils.contains('PACKAGECONFIG', 'openssl', '', 'CONFIG_EAP_PWD', d)}"

PACKAGECONFIG[dbus] = ",,dbus,"
LTRX_WPA_SUPPLICANT_INSTALL_DBUS = "${@bb.utils.contains('PACKAGECONFIG', 'dbus', 'install -D -m 0644 ${S}/wpa_supplicant/dbus/dbus-wpa_supplicant.conf ${D}${sysconfdir}/dbus-1/system.d/wpa_supplicant.conf; ${LTRX_WPA_SUPPLICANT_INSTALL_DBUS_OLD}; ${LTRX_WPA_SUPPLICANT_INSTALL_DBUS_NEW}', '', d)}"

PACKAGECONFIG[wpa-supplicant-dbus-old] = ",,,"
LTRX_WPA_SUPPLICANT_CONFIG_ENABLE += "${@bb.utils.contains('PACKAGECONFIG', 'wpa-supplicant-dbus-old', 'CONFIG_CTRL_IFACE_DBUS=', '', d)}"
LTRX_WPA_SUPPLICANT_INSTALL_DBUS_OLD = "${@bb.utils.contains('PACKAGECONFIG', 'wpa-supplicant-dbus-old', 'install -D ${S}/wpa_supplicant/dbus/${LTRX_WPA_SUPPLICANT_DBUS_OLD_SERVICE}.service ${D}${datadir}/dbus-1/system-services/${LTRX_WPA_SUPPLICANT_DBUS_OLD_SERVICE}.service', '', d)}"

PACKAGECONFIG[wpa-supplicant-dbus-new] = ",,,"
LTRX_WPA_SUPPLICANT_CONFIG_ENABLE += "${@bb.utils.contains('PACKAGECONFIG', 'wpa-supplicant-dbus-new', 'CONFIG_CTRL_IFACE_DBUS_NEW', '', d)}"
LTRX_WPA_SUPPLICANT_INSTALL_DBUS_NEW = "${@bb.utils.contains('PACKAGECONFIG', 'wpa-supplicant-dbus-new', 'install -D ${S}/wpa_supplicant/dbus/${LTRX_WPA_SUPPLICANT_DBUS_NEW_SERVICE}.service ${D}${datadir}/dbus-1/system-services/${LTRX_WPA_SUPPLICANT_DBUS_NEW_SERVICE}.service', '', d)}"

PACKAGECONFIG[wpa-supplicant-dbus-introspection] = ",,,"
LTRX_WPA_SUPPLICANT_CONFIG_ENABLE += "${@bb.utils.contains('PACKAGECONFIG', 'wpa-supplicant-dbus-introspection', 'CONFIG_CTRL_IFACE_DBUS_INTRO', '', d)}"

PACKAGECONFIG[wpa-supplicant-debug-syslog] = ",,,"
LTRX_WPA_SUPPLICANT_DEBUG_CONFIG = "${@bb.utils.contains('PACKAGECONFIG', 'wpa-supplicant-debug-syslog', 'sed "s/\(#\)\(CONFIG_DEBUG_SYSLOG.*\)/\2/" wpa_supplicant/${LTRX_WPA_SUPPLICANT_CONFIG}', '', d)}"

PACKAGECONFIG[readline] = ",,readline,"
LTRX_WPA_SUPPLICANT_CONFIG_ENABLE += "${@bb.utils.contains('PACKAGECONFIG', 'readline', 'CONFIG_READLINE', '', d)}"

PACKAGECONFIG[wpa-supplicant-cli] = ",,,"
LTRX_WPA_SUPPLICANT_INSTALL_CLI = "${@bb.utils.contains('PACKAGECONFIG', 'wpa-supplicant-cli', 'install -m 0755 -D ${S}/wpa_supplicant/wpa_cli ${D}${sbindir}/wpa_cli', '', d)}"

PACKAGECONFIG[wpa-supplicant-passphrase] = ",,,"
LTRX_WPA_SUPPLICANT_INSTALL_PASSPHRASE = "${@bb.utils.contains('PACKAGECONFIG', 'wpa-supplicant-passphrase', 'install -m 0755 -D ${S}/wpa_supplicant/wpa_passphrase ${D}${sbindir}/wpa_passphrase', '', d)}"

CFLAGS_append = " -I${STAGING_INCDIR}/libnl3"

do_configure() {
    rm -f wpa_supplicant/.config
    make -C wpa_supplicant clean

    install -m 0755 ${S}/wpa_supplicant/defconfig wpa_supplicant/.config
    for option in ${LTRX_WPA_SUPPLICANT_CONFIG_ENABLE}; do
        sed -i "s/^#\($option\)/\1/" wpa_supplicant/.config
    done
    for option in ${LTRX_WPA_SUPPLICANT_CONFIG_DISABLE}; do
        sed -i "s/^\($option\)/#\1/" wpa_supplicant/.config
    done
    sed -i 's/^#\CONFIG_TLS=openssl/CONFIG_TLS=${LTRX_WPA_SUPPLICANT_TLS}/' wpa_supplicant/.config

    rm -f wpa_supplicant/*.d wpa_supplicant/dbus/*.d
}

do_compile() {
    CFLAGS="${CFLAGS}" LDFLAGS="${LDFLAGS}" BINDIR="${sbindir}" \
        make CC="${CC}" -C wpa_supplicant
}

do_install() {
    install -m 0755 -D ${S}/wpa_supplicant/wpa_supplicant ${D}${sbindir}/wpa_supplicant
    install -m 0644 -D ${WORKDIR}/wpa_supplicant.conf ${D}${sysconfdir}/wpa_supplicant.conf
    ${LTRX_WPA_SUPPLICANT_INSTALL_CLI}
    ${LTRX_WPA_SUPPLICANT_INSTALL_PASSPHRASE}
    ${LTRX_WPA_SUPPLICANT_INSTALL_DBUS}
}
