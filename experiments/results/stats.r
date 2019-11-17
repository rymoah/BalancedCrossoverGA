data <- read.csv("summary.csv")
data <- na.omit(data)

names <- c("One Point", "Counter-based", "Zero-lengths", "Map of ones",
           "Counter-based w/ shuffle", "Zero-lengths w/ shuffle",
           "Map of ones w/shuffle")

short_names <- c("OP", "CB", "ZL", "MoO", "CB w/s", "ZL w/s", "MoO w/s")

pdf("boxplot.pdf")
boxplot(data[1:7], names=short_names, ylab="Best fitness at the last generation")
points(jitter(rep(1, 50), amount=0.1), data$noshuffle_cross0, pch=20, col=rgb(0,0,0,0.2))
points(jitter(rep(2, 50), amount=0.1), data$noshuffle_cross1, pch=20, col=rgb(0,0,0,0.2))
points(jitter(rep(3, 50), amount=0.1), data$noshuffle_cross2, pch=20, col=rgb(0,0,0,0.2))
points(jitter(rep(4, 50), amount=0.1), data$noshuffle_cross3, pch=20, col=rgb(0,0,0,0.2))
points(jitter(rep(5, 50), amount=0.1), data$shuffle_cross1, pch=20, col=rgb(0,0,0,0.2))
points(jitter(rep(6, 50), amount=0.1), data$shuffle_cross2, pch=20, col=rgb(0,0,0,0.2))
points(jitter(rep(7, 50), amount=0.1), data$shuffle_cross3, pch=20, col=rgb(0,0,0,0.2))
dev.off()

pdf("histogram.pdf")
colors = c("chocolate", "cornflowerblue", "darkseagreen",
           "goldenrod", "lightblue", "indianred", "mediumpurple")
par("mfcol"=c(2, 2))
breaks = c(floor(min(data)):ceiling(max(data)))
if (length(breaks) > 80) {
    breaks = seq(from=5*floor(min(data)/5), to=5*ceiling(max(data)/5), by=5)
}
if (length(breaks) < 4) {
    breaks = seq(from=floor(min(data)), to=ceiling(max(data)), by=0.5)
}
for (i in 1:7) {
    hist(data[[i]],
         col = colors[i],
         main = names[i],
         xlab="Fitness",
         xlim=c(floor(min(data)),ceiling( max(data))),
         breaks=breaks)
}
par("mfcol"=c(1, 1))
dev.off()

r <- matrix(rep(0, 49), nrow=7, ncol=7)
for (i in 1:7) {
    for (j in 1:7) {
        x <- data[[i]]
        y <- data[[j]]
        r[i,j] <- wilcox.test(x, y)$p.value
    }
}

expanded_names <- append(c(""), short_names)
write.table(r, "pvalues.csv", sep=",",
            col.names=short_names,
            row.names=FALSE)
