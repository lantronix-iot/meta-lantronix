SUMMARY = "BCMDHD WLAN driver for PW2050"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "\
    file://bcmutils.c;endline=24;md5=2813523c6d4bd69328124422096164d9 \
"

inherit module

SRC_URI = "file://${BP}.tar.gz"

COMPATIBLE_MACHINE_pw2050 = "pw2050"

do_install_append() {
    # Delete empty directories to avoid QA failures if no modules were built
    find ${D}/lib -depth -type d -empty -exec rmdir {} \;
}

python do_package_prepend() {
    if not os.path.exists(os.path.join(d.getVar('D', True), 'lib/modules')):
        bb.error("%s: no modules were created" % d.getVar('PN', True))
}

EXTRA_OEMAKE = " \
    CONFIG_BCMDHD=m \
    CONFIG_BCMDHD_NVRAM_PATH='/lib/modules/firmware/bcmdhd.cal' \
    CONFIG_BCMDHD_FW_PATH='/lib/modules/firmware/fw_bcmdhd.bin' \
    CONFIG_BCMDHD_SDIO=y \
"

do_compile() {
    oe_runmake -C ${STAGING_KERNEL_DIR} M=${B} modules
}

do_install() {
    install -d ${D}/lib
    oe_runmake -C ${STAGING_KERNEL_DIR} INSTALL_MOD_PATH=${D} M=${B} modules_install
}
