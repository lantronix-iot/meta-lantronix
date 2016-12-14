# This file was derived from the linux-yocto-custom.bb recipe in
# oe-core.
#
# linux-yocto-custom.bb:
#
#   A yocto-bsp-generated kernel recipe that uses the linux-yocto and
#   oe-core kernel classes to apply a subset of yocto kernel
#   management to git managed kernel repositories.
#
# Warning:
#
#   Building this kernel without providing a defconfig or BSP
#   configuration will result in build or boot errors. This is not a
#   bug.
#
# Notes:
#
#   patches: patches can be merged into to the source git tree itself,
#            added via the SRC_URI, or controlled via a BSP
#            configuration.
#
#   example configuration addition:
#            SRC_URI += "file://smp.cfg"
#   example patch addition:
#            SRC_URI += "file://0001-linux-version-tweak.patch
#   example feature addition:
#            SRC_URI += "file://feature.scc"
#

inherit kernel
require recipes-kernel/linux/linux-yocto.inc

# for git:
# SRC_URI = "git://${THISDIR}/${PN}/linux-at91.git;protocol=file;bareclone=1;branch=${KBRANCH}
# KBRANCH = "develop-boottime"
# PV = "${LINUX_VERSION}+git${SRCPV}"

SRC_URI = "file://linux-at91-${LTRX_PRODUCT_VERSION}_${LTRX_KERNEL_MACHINE}.tar.xz \
           file://defconfig \
           file://pw2050.scc \
           file://pw2050.cfg \
           file://pw2050-user-config.cfg \
           file://pw2050-user-patches.scc \
           "

# remove this if using git!
S = "${WORKDIR}/linux-at91-${LTRX_PRODUCT_VERSION}_${LTRX_KERNEL_MACHINE}"

LINUX_VERSION = "3.10.0"
LINUX_VERSION_EXTENSION = ""

PV = "${LINUX_VERSION}"

SRCREV="${AUTOREV}"

COMPATIBLE_MACHINE_pw2050 = "pw2050"
