data <- read.csv("all_results.csv", header=FALSE, sep="\t")
names <- c("One Point", "Counter-based", "Zero-lengths", "Map of ones")

pdf("plot-oa_16_15_2_4.pdf")
boxplot(data[1:4], names=names, ylab="Best fitness at the last generation")
points(jitter(rep(1, 50), amount=0.1), data$V1, pch=20, col=rgb(0,0,0,0.2))
points(jitter(rep(2, 50), amount=0.1), data$V2, pch=20, col=rgb(0,0,0,0.2))
points(jitter(rep(3, 50), amount=0.1), data$V3, pch=20, col=rgb(0,0,0,0.2))
points(jitter(rep(4, 50), amount=0.1), data$V4, pch=20, col=rgb(0,0,0,0.2))
dev.off()
pdf("plot-oa_16_8_2_4.pdf")
boxplot(data[5:8], names=names, ylab="Best fitness at the last generation")
points(jitter(rep(1, 50), amount=0.1), data$V5, pch=20, col=rgb(0,0,0,0.2))
points(jitter(rep(2, 50), amount=0.1), data$V6, pch=20, col=rgb(0,0,0,0.2))
points(jitter(rep(3, 50), amount=0.1), data$V7, pch=20, col=rgb(0,0,0,0.2))
points(jitter(rep(4, 50), amount=0.1), data$V8, pch=20, col=rgb(0,0,0,0.2))
dev.off()
pdf("plot-oa_16_8_3_2.pdf")
boxplot(data[9:12], names=names, ylab="Best fitness at the last generation")
points(jitter(rep(1, 50), amount=0.1), data$V9, pch=20, col=rgb(0,0,0,0.2))
points(jitter(rep(2, 50), amount=0.1), data$V10, pch=20, col=rgb(0,0,0,0.2))
points(jitter(rep(3, 50), amount=0.1), data$V11, pch=20, col=rgb(0,0,0,0.2))
points(jitter(rep(4, 50), amount=0.1), data$V12, pch=20, col=rgb(0,0,0,0.2))
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
