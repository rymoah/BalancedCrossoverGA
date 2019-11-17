data <- read.csv("all_results.csv", header=FALSE, sep="\t")
names <- c("One Point", "Counter-based", "Zero-lengths", "Map of ones")

pdf("plot-nvar6.pdf")
dat <- c(table(data$V1), table(data$V2), table(data$V3), table(data$V4))
d <- c(dat[1:6], 0, dat[7])
names(d) <- c(names(dat)[1:6], names(dat)[1], names(dat)[7])
dat <- d
xx  <- barplot(dat, beside=TRUE, ylim=c(0,52), ylab="Frequency", width=0.85, col=c("dark red", "dark blue"), xaxt = "n")
text(x = xx, y = 1, label = labels(dat), col="white")
text(x = xx, y = dat + 1, label = dat)
lpos = c((xx[1] + xx[2])/2, (xx[3] + xx[4])/2, (xx[5] + xx[6])/2, (xx[7] + xx[8])/2)
text(x = lpos, y = -2, label=names, xpd=NA)
dev.off()
pdf("plot-nvar7.pdf")
dat <- c(table(data$V5), table(data$V6), table(data$V7), table(data$V8))
d <- c(dat[1:6], 0, dat[7])
names(d) <- c(names(dat)[1:6], names(dat)[1], names(dat)[7])
dat <- d
xx  <- barplot(dat, beside=TRUE, ylim=c(0,52), ylab="Frequency", width=0.85, col=c("dark red", "dark blue"), xaxt = "n")
text(x = xx, y = 1, label = labels(dat), col="white")
text(x = xx, y = dat + 1, label = dat)
lpos = c((xx[1] + xx[2])/2, (xx[3] + xx[4])/2, (xx[5] + xx[6])/2, (xx[7] + xx[8])/2)
text(x = lpos, y = -2, label=names, xpd=NA)
dev.off()
pdf("plot-nvar8.pdf")
dat <- c(table(data$V9), table(data$V10), table(data$V11), table(data$V12))
d <- c(dat[1:6], 0, dat[7])
names(d) <- c(names(dat)[1:6], names(dat)[1], names(dat)[7])
dat <- d
xx  <- barplot(dat, beside=TRUE, ylim=c(0,52), ylab="Frequency", width=0.85, col=c("dark red", "dark blue"), xaxt = "n")
text(x = xx, y = 1, label = labels(dat), col="white")
text(x = xx, y = dat + 1, label = dat)
lpos = c((xx[1] + xx[2])/2, (xx[3] + xx[4])/2, (xx[5] + xx[6])/2, (xx[7] + xx[8])/2)
text(x = lpos, y = -2, label=names, xpd=NA)
dev.off()

r <- rep(0, 18)
r[1] <- wilcox.test(data$V1, data$V2)$p.value
r[2] <- wilcox.test(data$V1, data$V3)$p.value
r[3] <- wilcox.test(data$V1, data$V4)$p.value
r[4] <- wilcox.test(data$V2, data$V3)$p.value
r[5] <- wilcox.test(data$V2, data$V4)$p.value
r[6] <- wilcox.test(data$V3, data$V4)$p.value

r[7] <- wilcox.test(data$V5, data$V6)$p.value
r[8] <- wilcox.test(data$V5, data$V7)$p.value
r[9] <- wilcox.test(data$V5, data$V8)$p.value
r[10] <- wilcox.test(data$V6, data$V7)$p.value
r[11] <- wilcox.test(data$V6, data$V8)$p.value
r[12] <- wilcox.test(data$V7, data$V8)$p.value

r[13] <- wilcox.test(data$V9, data$V10)$p.value
r[14] <- wilcox.test(data$V9, data$V11)$p.value
r[15] <- wilcox.test(data$V9, data$V12)$p.value
r[16] <- wilcox.test(data$V10, data$V11)$p.value
r[17] <- wilcox.test(data$V10, data$V12)$p.value
r[18] <- wilcox.test(data$V11, data$V12)$p.value


write.table(r, "pvalues.csv", sep="\t", col.names=FALSE, row.names=FALSE)
